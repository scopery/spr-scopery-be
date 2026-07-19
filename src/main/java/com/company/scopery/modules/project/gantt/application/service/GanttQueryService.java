package com.company.scopery.modules.project.gantt.application.service;

import com.company.scopery.modules.project.gantt.application.query.GanttViewQuery;
import com.company.scopery.modules.project.gantt.application.response.*;
import com.company.scopery.modules.project.gantt.domain.enums.GanttGroupBy;
import com.company.scopery.modules.project.gantt.domain.enums.GanttItemScheduleStatus;
import com.company.scopery.modules.project.gantt.domain.enums.GanttItemType;
import com.company.scopery.modules.project.milestone.domain.enums.MilestoneStatus;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.project.application.response.ProjectResponse;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssue;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssueRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds a Gantt projection from Project / Phase / WBS / Task / Schedule / Milestone.
 * Never persists GanttItem rows and never includes finance fields.
 */
@Service
public class GanttQueryService {

    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectRepository projects;
    private final ProjectPhaseRepository phases;
    private final WbsNodeRepository wbsNodes;
    private final TaskRepository tasks;
    private final TaskDependencyRepository dependencies;
    private final ScheduleRunRepository runs;
    private final TaskScheduleRepository schedules;
    private final SchedulingIssueRepository issues;
    private final ProjectMilestoneRepository milestones;

    public GanttQueryService(ProjectWorkspaceAuthorizationService authorization,
                             ProjectRepository projects,
                             ProjectPhaseRepository phases,
                             WbsNodeRepository wbsNodes,
                             TaskRepository tasks,
                             TaskDependencyRepository dependencies,
                             ScheduleRunRepository runs,
                             TaskScheduleRepository schedules,
                             SchedulingIssueRepository issues,
                             ProjectMilestoneRepository milestones) {
        this.authorization = authorization;
        this.projects = projects;
        this.phases = phases;
        this.wbsNodes = wbsNodes;
        this.tasks = tasks;
        this.dependencies = dependencies;
        this.runs = runs;
        this.schedules = schedules;
        this.issues = issues;
        this.milestones = milestones;
    }

    @Transactional(readOnly = true)
    public GanttViewResponse getView(GanttViewQuery query) {
        authorization.requireGanttView(query.projectId());
        Project project = projects.findById(query.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(query.projectId()));

        ScheduleRun scheduleRun = resolveScheduleRun(project, query.scheduleRunId());
        GanttGroupBy groupBy = query.groupBy() == null || query.groupBy().isBlank()
                ? GanttGroupBy.PHASE
                : ProjectEnumParser.parseOptional(GanttGroupBy.class, query.groupBy(), "GANTT_INVALID_GROUP_BY", "groupBy");
        if (groupBy == null) {
            groupBy = GanttGroupBy.PHASE;
        }

        List<ProjectPhase> phaseList = phases.findAllByProjectId(project.id()).stream()
                .filter(p -> query.includeArchived() || p.status() != ProjectPhaseStatus.ARCHIVED)
                .toList();
        List<WbsNode> wbsList = wbsNodes.findAllByProjectId(project.id()).stream()
                .filter(n -> query.includeArchived() || n.status() != WbsNodeStatus.ARCHIVED)
                .toList();
        List<Task> taskList = tasks.findAllByProjectId(project.id()).stream()
                .filter(t -> query.includeArchived() || t.status() != TaskStatus.ARCHIVED)
                .toList();
        List<ProjectMilestone> milestoneList = milestones.findAllByProjectId(project.id()).stream()
                .filter(m -> query.includeArchived() || m.status() != MilestoneStatus.ARCHIVED)
                .toList();

        Map<UUID, TaskSchedule> scheduleByTask = new HashMap<>();
        List<SchedulingIssue> issueList = List.of();
        if (scheduleRun != null) {
            schedules.findAllByScheduleRunId(scheduleRun.id())
                    .forEach(s -> scheduleByTask.put(s.taskId(), s));
            issueList = issues.findAllByScheduleRunId(scheduleRun.id());
        }

        List<GanttItemResponse> items = buildItems(
                project, phaseList, wbsList, taskList, milestoneList, scheduleByTask,
                query.includeUnscheduled(), query.dateFrom(), query.dateTo(), groupBy);

        Set<UUID> taskIds = taskList.stream().map(Task::id).collect(Collectors.toSet());
        List<GanttDependencyResponse> deps = dependencies.findActiveByProjectId(project.id()).stream()
                .filter(d -> taskIds.contains(d.predecessorTaskId()) && taskIds.contains(d.successorTaskId()))
                .map(d -> toDependency(d))
                .toList();

        List<GanttIssueResponse> ganttIssues = issueList.stream()
                .map(i -> new GanttIssueResponse(i.id(), i.taskId(), i.userId(),
                        i.issueType().name(), i.severity().name(), i.message(), i.issueDate()))
                .toList();

        long scheduledTasks = items.stream()
                .filter(i -> GanttItemType.TASK.name().equals(i.itemType())
                        && GanttItemScheduleStatus.SCHEDULED.name().equals(i.scheduleStatus()))
                .count();
        long unscheduledTasks = items.stream()
                .filter(i -> GanttItemType.TASK.name().equals(i.itemType())
                        && (GanttItemScheduleStatus.UNSCHEDULED.name().equals(i.scheduleStatus())
                        || GanttItemScheduleStatus.PARTIAL.name().equals(i.scheduleStatus())))
                .count();
        long taskCount = items.stream().filter(i -> GanttItemType.TASK.name().equals(i.itemType())).count();
        long milestoneCount = items.stream().filter(i -> GanttItemType.MILESTONE.name().equals(i.itemType())).count();

        GanttSummaryResponse summary = new GanttSummaryResponse(
                items.size(),
                (int) taskCount,
                (int) scheduledTasks,
                (int) unscheduledTasks,
                (int) milestoneCount,
                deps.size(),
                ganttIssues.size());

        return new GanttViewResponse(
                ProjectResponse.from(project),
                scheduleRun != null ? ScheduleRunResponse.from(scheduleRun) : null,
                items,
                deps,
                ganttIssues,
                summary);
    }

    @Transactional(readOnly = true)
    public GanttCriticalPathResponse getCriticalPath(UUID projectId, UUID scheduleRunId) {
        authorization.requireGanttView(projectId);
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ScheduleRun scheduleRun = resolveScheduleRun(project, scheduleRunId);

        List<Task> taskList = tasks.findAllByProjectId(project.id()).stream()
                .filter(t -> t.status() != TaskStatus.ARCHIVED)
                .toList();
        Map<UUID, TaskSchedule> scheduleByTask = new HashMap<>();
        if (scheduleRun != null) {
            schedules.findAllByScheduleRunId(scheduleRun.id())
                    .forEach(s -> scheduleByTask.put(s.taskId(), s));
        }

        List<CriticalPathCalculator.TaskNode> nodes = new ArrayList<>();
        for (Task task : taskList) {
            TaskSchedule schedule = scheduleByTask.get(task.id());
            LocalDate start = schedule != null ? schedule.estimatedStartDate() : task.plannedStartDate();
            LocalDate finish = schedule != null ? schedule.estimatedFinishDate() : task.dueDate();
            int duration = CriticalPathCalculator.durationDays(start, finish);
            nodes.add(new CriticalPathCalculator.TaskNode(task.id(), task.title(), start, finish, duration));
        }

        Set<UUID> taskIds = taskList.stream().map(Task::id).collect(Collectors.toSet());
        List<CriticalPathCalculator.DependencyEdge> edges = dependencies.findActiveByProjectId(project.id()).stream()
                .filter(d -> taskIds.contains(d.predecessorTaskId()) && taskIds.contains(d.successorTaskId()))
                .map(d -> new CriticalPathCalculator.DependencyEdge(
                        d.predecessorTaskId(), d.successorTaskId(), d.lagDays(), d.dependencyType()))
                .toList();

        return CriticalPathCalculator.compute(
                project.id(),
                scheduleRun != null ? scheduleRun.id() : null,
                nodes,
                edges);
    }

    @Transactional(readOnly = true)
    public GanttExportResponse exportTasks(UUID projectId, UUID scheduleRunId, String format) {
        String normalized = format == null ? "" : format.trim().toUpperCase(Locale.ROOT);
        if (!"CSV".equals(normalized) && !"JSON".equals(normalized)) {
            throw ProjectExceptions.ganttInvalidExportFormat(format);
        }
        GanttViewResponse view = getView(new GanttViewQuery(projectId, scheduleRunId, null, null, true, false, "PHASE"));
        List<GanttExportTaskRowResponse> rows = view.items().stream()
                .filter(i -> GanttItemType.TASK.name().equals(i.itemType()))
                .map(i -> new GanttExportTaskRowResponse(
                        i.sourceEntityId(),
                        i.title(),
                        i.scheduleStatus(),
                        i.startDate(),
                        i.endDate(),
                        i.phaseId(),
                        i.wbsNodeId(),
                        i.assigneeUserId()))
                .toList();
        return new GanttExportResponse(normalized, rows);
    }

    public String exportTasksCsv(GanttExportResponse export) {
        StringBuilder csv = new StringBuilder("taskId,title,scheduleStatus,startDate,finishDate,projectPhaseId,wbsNodeId,inChargeUserId\n");
        for (GanttExportTaskRowResponse row : export.tasks()) {
            csv.append(csvCell(row.taskId())).append(',')
                    .append(csvCell(row.title())).append(',')
                    .append(csvCell(row.scheduleStatus())).append(',')
                    .append(csvCell(row.startDate())).append(',')
                    .append(csvCell(row.finishDate())).append(',')
                    .append(csvCell(row.projectPhaseId())).append(',')
                    .append(csvCell(row.wbsNodeId())).append(',')
                    .append(csvCell(row.inChargeUserId())).append('\n');
        }
        return csv.toString();
    }

    private static String csvCell(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private ScheduleRun resolveScheduleRun(Project project, UUID scheduleRunId) {
        if (scheduleRunId != null) {
            ScheduleRun run = runs.findById(scheduleRunId)
                    .orElseThrow(() -> ProjectExceptions.ganttScheduleRunNotFound(scheduleRunId));
            if (!run.projectId().equals(project.id())) {
                throw ProjectExceptions.ganttScheduleRunProjectMismatch(scheduleRunId, project.id());
            }
            return run;
        }
        if (project.currentScheduleRunId() != null) {
            Optional<ScheduleRun> current = runs.findById(project.currentScheduleRunId());
            if (current.isPresent() && current.get().projectId().equals(project.id())) {
                return current.get();
            }
        }
        return runs.findAllByProjectId(project.id()).stream()
                .filter(r -> r.status() == ScheduleRunStatus.COMPLETED)
                .findFirst()
                .orElse(null);
    }

    private List<GanttItemResponse> buildItems(
            Project project,
            List<ProjectPhase> phaseList,
            List<WbsNode> wbsList,
            List<Task> taskList,
            List<ProjectMilestone> milestoneList,
            Map<UUID, TaskSchedule> scheduleByTask,
            boolean includeUnscheduled,
            LocalDate dateFrom,
            LocalDate dateTo,
            GanttGroupBy groupBy) {

        List<GanttItemResponse> items = new ArrayList<>();
        String projectItemId = "PROJECT:" + project.id();

        Map<UUID, List<Task>> tasksByPhase = taskList.stream()
                .filter(t -> t.projectPhaseId() != null)
                .collect(Collectors.groupingBy(Task::projectPhaseId));
        Map<UUID, List<Task>> tasksByWbs = taskList.stream()
                .filter(t -> t.wbsNodeId() != null)
                .collect(Collectors.groupingBy(Task::wbsNodeId));

        LocalDate projectStart = project.plannedStartDate();
        LocalDate projectEnd = project.plannedEndDate();
        for (TaskSchedule s : scheduleByTask.values()) {
            projectStart = minDate(projectStart, s.estimatedStartDate());
            projectEnd = maxDate(projectEnd, s.estimatedFinishDate());
        }
        for (ProjectMilestone m : milestoneList) {
            projectStart = minDate(projectStart, m.milestoneDate());
            projectEnd = maxDate(projectEnd, m.milestoneDate());
        }

        items.add(new GanttItemResponse(
                projectItemId, GanttItemType.PROJECT.name(), "PROJECT", project.id(),
                null, project.name(), projectStart, projectEnd,
                GanttItemScheduleStatus.NOT_APPLICABLE.name(),
                null, null, null, 0, false, Map.of()));

        Map<UUID, String> phaseItemIds = new HashMap<>();
        if (groupBy == GanttGroupBy.PHASE) {
            for (ProjectPhase phase : phaseList) {
                String phaseItemId = "PHASE:" + phase.id();
                phaseItemIds.put(phase.id(), phaseItemId);
                LocalDate start = phase.plannedStartDate();
                LocalDate end = phase.plannedEndDate();
                for (Task t : tasksByPhase.getOrDefault(phase.id(), List.of())) {
                    TaskSchedule s = scheduleByTask.get(t.id());
                    if (s != null) {
                        start = minDate(start, s.estimatedStartDate());
                        end = maxDate(end, s.estimatedFinishDate());
                    } else {
                        start = minDate(start, t.plannedStartDate());
                        end = maxDate(end, t.dueDate());
                    }
                }
                items.add(new GanttItemResponse(
                        phaseItemId, GanttItemType.PHASE.name(), "PROJECT_PHASE", phase.id(),
                        projectItemId, phase.name(), start, end,
                        GanttItemScheduleStatus.NOT_APPLICABLE.name(),
                        null, phase.id(), null, phase.displayOrder(), false, Map.of()));
            }
        }

        Map<UUID, String> wbsItemIds = new HashMap<>();
        for (WbsNode node : wbsList) {
            String wbsItemId = "WBS:" + node.id();
            wbsItemIds.put(node.id(), wbsItemId);
            String parentItemId = node.parentId() != null
                    ? wbsItemIds.getOrDefault(node.parentId(),
                    phaseItemIds.getOrDefault(node.projectPhaseId(), projectItemId))
                    : phaseItemIds.getOrDefault(node.projectPhaseId(), projectItemId);
            LocalDate start = null;
            LocalDate end = null;
            for (Task t : subtreeTasks(node, wbsList, taskList)) {
                TaskSchedule s = scheduleByTask.get(t.id());
                if (s != null) {
                    start = minDate(start, s.estimatedStartDate());
                    end = maxDate(end, s.estimatedFinishDate());
                }
            }
            items.add(new GanttItemResponse(
                    wbsItemId, GanttItemType.WBS_NODE.name(), "WBS_NODE", node.id(),
                    parentItemId, node.title(), start, end,
                    GanttItemScheduleStatus.NOT_APPLICABLE.name(),
                    null, node.projectPhaseId(), node.id(), node.sortOrder(), false, Map.of()));
        }

        for (Task task : taskList) {
            TaskSchedule schedule = scheduleByTask.get(task.id());
            LocalDate start = schedule != null ? schedule.estimatedStartDate() : null;
            LocalDate end = schedule != null ? schedule.estimatedFinishDate() : null;
            GanttItemScheduleStatus status = resolveTaskScheduleStatus(schedule);
            if (!includeUnscheduled && status == GanttItemScheduleStatus.UNSCHEDULED) {
                continue;
            }
            if (!inDateWindow(start, end, dateFrom, dateTo) && status != GanttItemScheduleStatus.UNSCHEDULED) {
                continue;
            }
            String parentItemId = task.wbsNodeId() != null
                    ? wbsItemIds.getOrDefault(task.wbsNodeId(),
                    phaseItemIds.getOrDefault(task.projectPhaseId(), projectItemId))
                    : phaseItemIds.getOrDefault(task.projectPhaseId(), projectItemId);
            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("taskStatus", task.status().name());
            meta.put("priority", task.priority() != null ? task.priority().name() : null);
            // Intentionally omit finance/quote/cost/baseline fields.
            items.add(new GanttItemResponse(
                    "TASK:" + task.id(), GanttItemType.TASK.name(), "TASK", task.id(),
                    parentItemId, task.title(), start, end, status.name(),
                    task.inChargeUserId(), task.projectPhaseId(), task.wbsNodeId(),
                    null, false, meta));
        }

        for (ProjectMilestone milestone : milestoneList) {
            if (!inDateWindow(milestone.milestoneDate(), milestone.milestoneDate(), dateFrom, dateTo)) {
                continue;
            }
            String parentItemId = milestone.wbsNodeId() != null
                    ? wbsItemIds.getOrDefault(milestone.wbsNodeId(),
                    phaseItemIds.getOrDefault(milestone.projectPhaseId(), projectItemId))
                    : phaseItemIds.getOrDefault(milestone.projectPhaseId(), projectItemId);
            items.add(new GanttItemResponse(
                    "MILESTONE:" + milestone.id(), GanttItemType.MILESTONE.name(), "PROJECT_MILESTONE",
                    milestone.id(), parentItemId, milestone.name(),
                    milestone.milestoneDate(), milestone.milestoneDate(),
                    GanttItemScheduleStatus.NOT_APPLICABLE.name(),
                    null, milestone.projectPhaseId(), milestone.wbsNodeId(),
                    milestone.sortOrder(), true, Map.of("milestoneStatus", milestone.status().name())));
        }

        return items;
    }

    private List<Task> subtreeTasks(WbsNode node, List<WbsNode> allNodes, List<Task> allTasks) {
        Set<UUID> nodeIds = new HashSet<>();
        nodeIds.add(node.id());
        boolean changed = true;
        while (changed) {
            changed = false;
            for (WbsNode n : allNodes) {
                if (n.parentId() != null && nodeIds.contains(n.parentId()) && nodeIds.add(n.id())) {
                    changed = true;
                }
            }
        }
        return allTasks.stream().filter(t -> t.wbsNodeId() != null && nodeIds.contains(t.wbsNodeId())).toList();
    }

    private GanttItemScheduleStatus resolveTaskScheduleStatus(TaskSchedule schedule) {
        if (schedule == null) {
            return GanttItemScheduleStatus.UNSCHEDULED;
        }
        return switch (schedule.scheduleStatus()) {
            case SCHEDULED -> GanttItemScheduleStatus.SCHEDULED;
            case PARTIALLY_SCHEDULED -> GanttItemScheduleStatus.PARTIAL;
            case BLOCKED -> GanttItemScheduleStatus.BLOCKED;
            default -> GanttItemScheduleStatus.UNSCHEDULED;
        };
    }

    private GanttDependencyResponse toDependency(TaskDependency d) {
        boolean unsupported = d.dependencyType() != TaskDependencyType.FINISH_TO_START;
        return new GanttDependencyResponse(
                d.id(),
                d.predecessorTaskId(),
                d.successorTaskId(),
                "TASK:" + d.predecessorTaskId(),
                "TASK:" + d.successorTaskId(),
                d.dependencyType().name(),
                d.lagDays(),
                unsupported);
    }

    private boolean inDateWindow(LocalDate start, LocalDate end, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return true;
        }
        if (start == null && end == null) {
            return true;
        }
        LocalDate s = start != null ? start : end;
        LocalDate e = end != null ? end : start;
        if (from != null && e != null && e.isBefore(from)) {
            return false;
        }
        if (to != null && s != null && s.isAfter(to)) {
            return false;
        }
        return true;
    }

    private LocalDate minDate(LocalDate a, LocalDate b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isBefore(b) ? a : b;
    }

    private LocalDate maxDate(LocalDate a, LocalDate b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }
}
