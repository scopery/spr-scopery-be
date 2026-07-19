package com.company.scopery.modules.project.scheduling.engine;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.*;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.*;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.*;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.*;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.*;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.*;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.*;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ScheduleEngineService {
    public static final int MAX_PLANNING_DAYS = 365;
    public static final String ALGORITHM_VERSION = "1.0.0";
    private final TaskRepository tasks;
    private final TaskDependencyRepository dependencies;
    private final ScheduleRunRepository runs;
    private final TaskScheduleRepository schedules;
    private final ScheduledDailyWorkRepository dailyWork;
    private final SchedulingIssueRepository issues;
    private final SchedulingCapacityResolver capacityResolver;
    private final ProjectRepository projects;
    private final TaskScheduleOverrideRepository overrides;

    public ScheduleEngineService(TaskRepository tasks, TaskDependencyRepository dependencies,
            ScheduleRunRepository runs, TaskScheduleRepository schedules,
            ScheduledDailyWorkRepository dailyWork, SchedulingIssueRepository issues,
            SchedulingCapacityResolver capacityResolver, ProjectRepository projects,
            TaskScheduleOverrideRepository overrides) {
        this.tasks=tasks;this.dependencies=dependencies;this.runs=runs;this.schedules=schedules;
        this.dailyWork=dailyWork;this.issues=issues;this.capacityResolver=capacityResolver;this.projects=projects;
        this.overrides=overrides;
    }

    @Transactional
    public ScheduleRun execute(ScheduleRun run, boolean includeCompletedTasks, boolean markAsCurrent) {
        ScheduleRun running=runs.save(run.running());
        List<Task> selected=tasks.findAllByProjectId(run.projectId()).stream()
                .filter(t -> includeCompletedTasks || !Set.of(TaskStatus.DONE,TaskStatus.CANCELLED,TaskStatus.ARCHIVED).contains(t.status()))
                .toList();
        Map<UUID,Task> byId=new LinkedHashMap<>(); selected.forEach(t -> byId.put(t.id(),t));
        List<TaskDependency> deps=dependencies.findActiveByProjectId(run.projectId()).stream()
                .filter(d -> byId.containsKey(d.predecessorTaskId())&&byId.containsKey(d.successorTaskId())).toList();
        List<Task> ordered=topologicalSort(selected,deps);
        if(ordered.size()!=selected.size()) {
            SchedulingIssue cycle=SchedulingIssue.create(run.id(),run.projectId(),null,null,null,
                    SchedulingIssueType.TASK_DEPENDENCY_CYCLE,SchedulingIssueSeverity.BLOCKER,
                    "Task dependency cycle detected",null);
            issues.saveAll(List.of(cycle));
            return runs.save(running.failed("SCHEDULE_DEPENDENCY_CYCLE_DETECTED","Task dependency cycle detected"));
        }

        Map<UUID,TaskScheduleOverride> overrideByTask=new HashMap<>();
        overrides.findActiveByProjectId(run.projectId()).forEach(o -> overrideByTask.put(o.taskId(), o));

        List<TaskSchedule> resultSchedules=new ArrayList<>();
        List<ScheduledDailyWork> work=new ArrayList<>();
        List<SchedulingIssue> resultIssues=new ArrayList<>();
        Map<UUID,TaskSchedule> scheduleByTask=new HashMap<>();
        Map<UUID,SchedulingCapacityResolver.CapacityResolution> resolutionByUser=new HashMap<>();
        Map<UUID,Map<LocalDate,BigDecimal>> remainingByUser=new HashMap<>();
        Map<UUID,List<TaskDependency>> incoming=new HashMap<>();
        deps.forEach(d -> incoming.computeIfAbsent(d.successorTaskId(),k->new ArrayList<>()).add(d));

        for(Task task:ordered) {
            UUID userId=task.inChargeUserId();
            BigDecimal estimate=task.estimateHours();
            TaskScheduleOverride override=overrideByTask.get(task.id());
            LocalDate effectiveDue=resolveEffectiveDueDate(task, override);
            if(override!=null) {
                resultIssues.add(issue(run,task,userId,null,SchedulingIssueType.MANUAL_SCHEDULE_OVERRIDE_APPLIED,
                        SchedulingIssueSeverity.INFO,"Manual schedule override applied: "+override.overrideType()));
            }
            if(userId==null) {
                TaskSchedule s=unscheduled(run,task,TaskScheduleRiskStatus.NO_ASSIGNEE,estimate,effectiveDue);
                resultSchedules.add(s);scheduleByTask.put(task.id(),s);
                resultIssues.add(issue(run,task,null,null,SchedulingIssueType.TASK_NO_ASSIGNEE,SchedulingIssueSeverity.ERROR,"Task has no in-charge user"));
                continue;
            }
            SchedulingCapacityResolver.CapacityResolution resolution=resolutionByUser.computeIfAbsent(userId,
                    id -> capacityResolver.resolve(run.workspaceId(),run.projectId(),id,run.planningStartDate(),run.planningEndDate()));
            if(!resolution.activeMember()) {
                TaskSchedule s=unscheduled(run,task,TaskScheduleRiskStatus.NO_ASSIGNEE,estimate,effectiveDue);
                resultSchedules.add(s);scheduleByTask.put(task.id(),s);
                resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.TASK_NO_ASSIGNEE,SchedulingIssueSeverity.ERROR,"In-charge user is not an active workspace member"));
                continue;
            }
            if(estimate==null||estimate.signum()<=0) {
                TaskSchedule s=unscheduled(run,task,TaskScheduleRiskStatus.UNSCHEDULED,estimate,effectiveDue);
                resultSchedules.add(s);scheduleByTask.put(task.id(),s);
                resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),estimate==null?SchedulingIssueType.TASK_NO_ESTIMATE:SchedulingIssueType.TASK_INVALID_ESTIMATE,SchedulingIssueSeverity.ERROR,"Task estimate must be greater than zero"));
                continue;
            }
            if(!resolution.calendarPresent()) resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.CALENDAR_MISSING,SchedulingIssueSeverity.WARNING,"No working calendar; lazy weekday defaults used"));
            if(!resolution.allocationPresent()) resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.ALLOCATION_MISSING,SchedulingIssueSeverity.ERROR,"No active project allocation; capacity is zero"));

            LocalDate earliest=max(run.planningStartDate(),task.plannedStartDate());
            boolean blocked=false;
            for(TaskDependency dep:incoming.getOrDefault(task.id(),List.of())) {
                if(dep.dependencyType()!=TaskDependencyType.FINISH_TO_START) {
                    resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.UNSUPPORTED_DEPENDENCY_TYPE,SchedulingIssueSeverity.WARNING,"Unsupported dependency type: "+dep.dependencyType()));
                    continue;
                }
                TaskSchedule pred=scheduleByTask.get(dep.predecessorTaskId());
                if(pred==null||pred.estimatedFinishDate()==null||pred.unscheduledHours().signum()>0) {
                    blocked=true;
                    resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.TASK_DEPENDENCY_UNSCHEDULED,SchedulingIssueSeverity.ERROR,"Predecessor is unscheduled"));
                } else earliest=max(earliest,pred.estimatedFinishDate().plusDays(dep.lagDays()));
            }
            // Hard PIN: earliest = max(depEarliest, max(planningStart, manualStartDate))
            if(override!=null && override.manualStartDate()!=null
                    && (override.overrideType()==ScheduleOverrideType.PIN_START
                    || override.overrideType()==ScheduleOverrideType.PIN_RANGE)) {
                earliest=max(earliest, max(run.planningStartDate(), override.manualStartDate()));
            }
            if(blocked) {
                TaskSchedule s=TaskSchedule.create(run.id(),run.projectId(),task.id(),userId,resolution.workspaceMemberId(),null,null,
                        BigDecimal.ZERO,estimate,effectiveDue,BigDecimal.ZERO,TaskScheduleRiskStatus.BLOCKED_BY_DEPENDENCY,TaskScheduleStatus.BLOCKED);
                resultSchedules.add(s);scheduleByTask.put(task.id(),s);continue;
            }

            LocalDate planningEnd=run.planningEndDate();
            if(override!=null && override.manualFinishDate()!=null
                    && (override.overrideType()==ScheduleOverrideType.PIN_FINISH
                    || override.overrideType()==ScheduleOverrideType.PIN_RANGE)) {
                planningEnd=min(planningEnd, override.manualFinishDate());
            }

            Map<LocalDate,BigDecimal> remaining=remainingByUser.computeIfAbsent(userId,id -> {
                Map<LocalDate,BigDecimal> map=new LinkedHashMap<>();
                resolution.days().forEach((date,day)->map.put(date,day.projectAllocatedHours()));
                return map;
            });
            BigDecimal availableBeforeDue=BigDecimal.ZERO;
            if(effectiveDue!=null) for(LocalDate d=earliest;!d.isAfter(effectiveDue)&&!d.isAfter(planningEnd);d=d.plusDays(1))
                availableBeforeDue=availableBeforeDue.add(remaining.getOrDefault(d,BigDecimal.ZERO));
            BigDecimal gap=effectiveDue==null?BigDecimal.ZERO:estimate.subtract(availableBeforeDue).max(BigDecimal.ZERO);
            BigDecimal unallocated=estimate;LocalDate first=null,last=null;
            UUID scheduleId=UUID.randomUUID();
            for(LocalDate date=earliest;unallocated.signum()>0&&!date.isAfter(planningEnd);date=date.plusDays(1)) {
                BigDecimal capacity=remaining.getOrDefault(date,BigDecimal.ZERO);
                if(capacity.signum()<=0) continue;
                BigDecimal planned=unallocated.min(capacity);
                remaining.put(date,capacity.subtract(planned));
                if(first==null)first=date;last=date;unallocated=unallocated.subtract(planned);
                work.add(new ScheduledDailyWork(UUID.randomUUID(),run.id(),scheduleId,run.projectId(),task.id(),
                        resolution.workspaceMemberId(),userId,date,planned,capacity,capacity.subtract(planned),null,null));
            }
            BigDecimal scheduled=estimate.subtract(unallocated);
            TaskScheduleStatus status=unallocated.signum()==0?TaskScheduleStatus.SCHEDULED:
                    scheduled.signum()>0?TaskScheduleStatus.PARTIALLY_SCHEDULED:TaskScheduleStatus.UNSCHEDULED;
            TaskScheduleRiskStatus risk=deriveRisk(effectiveDue,last,gap,unallocated);
            TaskSchedule s=new TaskSchedule(scheduleId,run.id(),run.projectId(),task.id(),userId,resolution.workspaceMemberId(),
                    first,last,scheduled,unallocated,effectiveDue,gap,risk,status,null,null);
            resultSchedules.add(s);scheduleByTask.put(task.id(),s);
            if(gap.signum()>0) resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.TASK_DUE_DATE_CAPACITY_GAP,SchedulingIssueSeverity.WARNING,"Insufficient capacity before due date"));
            if(unallocated.signum()>0) {
                resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.TASK_NO_CAPACITY,SchedulingIssueSeverity.ERROR,"Task cannot be fully scheduled in available capacity"));
                resultIssues.add(issue(run,task,userId,resolution.workspaceMemberId(),SchedulingIssueType.PLANNING_HORIZON_EXCEEDED,SchedulingIssueSeverity.WARNING,"Task exceeds planning horizon"));
            }
        }
        schedules.saveAll(resultSchedules);
        dailyWork.saveAll(work);
        issues.saveAll(resultIssues);

        long scheduledCount = resultSchedules.stream().filter(s -> s.scheduleStatus() == TaskScheduleStatus.SCHEDULED).count();
        long unscheduledCount = resultSchedules.stream()
                .filter(s -> s.scheduleStatus() == TaskScheduleStatus.UNSCHEDULED
                        || s.scheduleStatus() == TaskScheduleStatus.PARTIALLY_SCHEDULED)
                .count();
        long atRiskCount = resultSchedules.stream()
                .filter(s -> s.riskStatus() == TaskScheduleRiskStatus.AT_RISK)
                .count();
        long overdueCount = resultSchedules.stream()
                .filter(s -> s.riskStatus() == TaskScheduleRiskStatus.OVERDUE)
                .count();
        BigDecimal total = resultSchedules.stream()
                .map(TaskSchedule::scheduledHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String summary = "{"
                + "\"totalTasks\":" + resultSchedules.size() + ","
                + "\"scheduledTasks\":" + scheduledCount + ","
                + "\"unscheduledTasks\":" + unscheduledCount + ","
                + "\"atRiskTasks\":" + atRiskCount + ","
                + "\"overdueTasks\":" + overdueCount + ","
                + "\"totalScheduledHours\":" + total + ","
                + "\"issueCount\":" + resultIssues.size()
                + "}";
        ScheduleRun completed = runs.save(running.completed(summary));
        if (markAsCurrent) {
            projects.findById(run.projectId()).ifPresent(p -> projects.save(p.withCurrentScheduleRunId(run.id())));
        }
        return completed;
    }

    private LocalDate resolveEffectiveDueDate(Task task, TaskScheduleOverride override) {
        if (override != null
                && override.overrideType() == ScheduleOverrideType.DUE_DATE_OVERRIDE
                && override.manualDueDate() != null) {
            return override.manualDueDate();
        }
        return task.dueDate();
    }

    private List<Task> topologicalSort(List<Task> taskList,List<TaskDependency> deps) {
        Map<UUID,Integer> degree=new HashMap<>();Map<UUID,List<UUID>> outgoing=new HashMap<>();
        taskList.forEach(t->degree.put(t.id(),0));
        deps.forEach(d->{outgoing.computeIfAbsent(d.predecessorTaskId(),k->new ArrayList<>()).add(d.successorTaskId());degree.computeIfPresent(d.successorTaskId(),(k,v)->v+1);});
        Deque<UUID> q=new ArrayDeque<>();degree.forEach((id,n)->{if(n==0)q.add(id);});Map<UUID,Task> map=new HashMap<>();taskList.forEach(t->map.put(t.id(),t));
        List<Task> result=new ArrayList<>();while(!q.isEmpty()){UUID id=q.remove();result.add(map.get(id));for(UUID next:outgoing.getOrDefault(id,List.of()))if(degree.compute(next,(k,v)->v-1)==0)q.add(next);}return result;
    }
    private TaskSchedule unscheduled(ScheduleRun r,Task t,TaskScheduleRiskStatus risk,BigDecimal estimate,LocalDate due){
        return TaskSchedule.create(r.id(),r.projectId(),t.id(),t.inChargeUserId(),null,null,null,BigDecimal.ZERO,estimate==null?BigDecimal.ZERO:estimate,due,BigDecimal.ZERO,risk,TaskScheduleStatus.UNSCHEDULED);
    }
    private SchedulingIssue issue(ScheduleRun r,Task t,UUID u,UUID m,SchedulingIssueType type,SchedulingIssueSeverity severity,String message){return SchedulingIssue.create(r.id(),r.projectId(),t.id(),u,m,type,severity,message,null);}
    private LocalDate max(LocalDate a,LocalDate b){return b!=null&&b.isAfter(a)?b:a;}
    private LocalDate min(LocalDate a,LocalDate b){return b!=null&&b.isBefore(a)?b:a;}
    private TaskScheduleRiskStatus deriveRisk(LocalDate due,LocalDate finish,BigDecimal gap,BigDecimal remaining){if(remaining.signum()>0)return TaskScheduleRiskStatus.NO_CAPACITY;if(due!=null&&LocalDate.now().isAfter(due))return TaskScheduleRiskStatus.OVERDUE;if(due!=null&&((finish!=null&&finish.isAfter(due))||gap.signum()>0))return TaskScheduleRiskStatus.AT_RISK;return TaskScheduleRiskStatus.ON_TRACK;}
}
