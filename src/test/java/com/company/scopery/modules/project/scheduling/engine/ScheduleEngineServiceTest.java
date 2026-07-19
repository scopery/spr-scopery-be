package com.company.scopery.modules.project.scheduling.engine;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.scheduling.scheduleddailywork.domain.model.*;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.enums.ScheduleRunStatus;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.*;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums.SchedulingIssueType;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.*;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.*;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.*;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleEngineServiceTest {
    @Mock TaskRepository tasks; @Mock TaskDependencyRepository dependencies; @Mock ScheduleRunRepository runs;
    @Mock TaskScheduleRepository schedules; @Mock ScheduledDailyWorkRepository dailyWork;
    @Mock SchedulingIssueRepository issues; @Mock SchedulingCapacityResolver capacityResolver; @Mock ProjectRepository projects;
    @Mock TaskScheduleOverrideRepository overrides;
    ScheduleEngineService engine;
    UUID projectId=UUID.randomUUID(),workspaceId=UUID.randomUUID(),userId=UUID.randomUUID(),memberId=UUID.randomUUID();
    LocalDate start=LocalDate.of(2026,8,3),end=start.plusDays(10);

    @BeforeEach void setUp(){
        engine=new ScheduleEngineService(tasks,dependencies,runs,schedules,dailyWork,issues,capacityResolver,projects,overrides);
        when(runs.save(any())).thenAnswer(i->i.getArgument(0));
        lenient().when(schedules.saveAll(any())).thenAnswer(i->i.getArgument(0));
        lenient().when(dailyWork.saveAll(any())).thenAnswer(i->i.getArgument(0));
        when(issues.saveAll(any())).thenAnswer(i->i.getArgument(0));
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of());
        lenient().when(overrides.findActiveByProjectId(projectId)).thenReturn(List.of());
    }

    @Test void happyPathFinishToStartAndCapacity(){
        Task first=task("A",userId,new BigDecimal("6"),start.plusDays(3));
        Task second=task("B",userId,new BigDecimal("3"),start.plusDays(6));
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(first,second));
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of(TaskDependency.create(projectId,first.id(),second.id(),TaskDependencyType.FINISH_TO_START,1)));
        when(capacityResolver.resolve(any(),any(),eq(userId),any(),any())).thenReturn(capacity(true,true,Map.of(start,new BigDecimal("6"),start.plusDays(1),new BigDecimal("6"),start.plusDays(2),new BigDecimal("6"))));
        ScheduleRun result=engine.execute(run(),false,false);
        assertThat(result.status()).isEqualTo(ScheduleRunStatus.COMPLETED);
        ArgumentCaptor<List<TaskSchedule>> captor=ArgumentCaptor.forClass(List.class);verify(schedules).saveAll(captor.capture());
        assertThat(captor.getValue().get(1).estimatedStartDate()).isAfter(captor.getValue().get(0).estimatedFinishDate());
    }

    @Test void nonWorkingDayIsSkipped(){
        Task task=task("A",userId,new BigDecimal("2"),null);when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,true,Map.of(start,BigDecimal.ZERO,start.plusDays(1),new BigDecimal("2"))));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<ScheduledDailyWork>> captor=ArgumentCaptor.forClass(List.class);verify(dailyWork).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(ScheduledDailyWork::workDate).containsExactly(start.plusDays(1));
    }

    @Test void noAssigneeAndNoEstimateCreateIssues(){
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task("A",null,new BigDecimal("2"),null),task("B",userId,null,null)));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,true,Map.of()));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<SchedulingIssue>> captor=ArgumentCaptor.forClass(List.class);verify(issues).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(SchedulingIssue::issueType).contains(SchedulingIssueType.TASK_NO_ASSIGNEE,SchedulingIssueType.TASK_NO_ESTIMATE);
    }

    @Test void noAllocationMeansZeroCapacity(){
        Task task=task("A",userId,new BigDecimal("2"),null);when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,false,Map.of(start,new BigDecimal("8"))));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<SchedulingIssue>> captor=ArgumentCaptor.forClass(List.class);verify(issues).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(SchedulingIssue::issueType).contains(SchedulingIssueType.ALLOCATION_MISSING);
    }

    @Test void cycleFailsRun(){
        Task a=task("A",userId,BigDecimal.ONE,null),b=task("B",userId,BigDecimal.ONE,null);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(a,b));
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of(TaskDependency.create(projectId,a.id(),b.id(),TaskDependencyType.FINISH_TO_START,0),TaskDependency.create(projectId,b.id(),a.id(),TaskDependencyType.FINISH_TO_START,0)));
        assertThat(engine.execute(run(),false,false).status()).isEqualTo(ScheduleRunStatus.FAILED);
    }

    @Test void unsupportedDependencyCreatesWarning(){
        Task a=task("A",userId,BigDecimal.ONE,null),b=task("B",userId,BigDecimal.ONE,null);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(a,b));
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of(TaskDependency.create(projectId,a.id(),b.id(),TaskDependencyType.START_TO_START,0)));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,true,Map.of(start,new BigDecimal("8"))));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<SchedulingIssue>> captor=ArgumentCaptor.forClass(List.class);verify(issues).saveAll(captor.capture());
        assertThat(captor.getValue()).extracting(SchedulingIssue::issueType).contains(SchedulingIssueType.UNSUPPORTED_DEPENDENCY_TYPE);
    }

    @Test void dueDateGapIsAtRisk(){
        Task task=task("A",userId,new BigDecimal("8"),start);when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,true,Map.of(start,new BigDecimal("2"),start.plusDays(1),new BigDecimal("6"))));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<TaskSchedule>> captor=ArgumentCaptor.forClass(List.class);verify(schedules).saveAll(captor.capture());
        assertThat(captor.getValue().getFirst().riskStatus()).isEqualTo(TaskScheduleRiskStatus.AT_RISK);
    }

    @Test void activeOverridePinsStart(){
        Task task=task("A",userId,new BigDecimal("2"),null);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(task));
        LocalDate pinStart=start.plusDays(2);
        when(overrides.findActiveByProjectId(projectId)).thenReturn(List.of(
                TaskScheduleOverride.create(projectId, task.id(), ScheduleOverrideType.PIN_START,
                        pinStart, null, null, "manual pin")));
        when(capacityResolver.resolve(any(),any(),any(),any(),any())).thenReturn(capacity(true,true,Map.of(
                start,new BigDecimal("8"), start.plusDays(1),new BigDecimal("8"),
                start.plusDays(2),new BigDecimal("8"), start.plusDays(3),new BigDecimal("8"))));
        engine.execute(run(),false,false);
        ArgumentCaptor<List<TaskSchedule>> scheduleCaptor=ArgumentCaptor.forClass(List.class);
        verify(schedules).saveAll(scheduleCaptor.capture());
        assertThat(scheduleCaptor.getValue().getFirst().estimatedStartDate()).isEqualTo(pinStart);
        ArgumentCaptor<List<SchedulingIssue>> issueCaptor=ArgumentCaptor.forClass(List.class);
        verify(issues).saveAll(issueCaptor.capture());
        assertThat(issueCaptor.getValue()).extracting(SchedulingIssue::issueType)
                .contains(SchedulingIssueType.MANUAL_SCHEDULE_OVERRIDE_APPLIED);
    }

    private ScheduleRun run(){return ScheduleRun.create(projectId,workspaceId,start,end,"{}",UUID.randomUUID(),null);}
    private Task task(String code,UUID user,BigDecimal estimate,LocalDate due){return Task.create(projectId,null,null,code,code,null,user,null,null,estimate,start,due,TaskPriority.MEDIUM);}
    private SchedulingCapacityResolver.CapacityResolution capacity(boolean active,boolean allocated,Map<LocalDate,BigDecimal> hours){
        Map<LocalDate,SchedulingCapacityResolver.DayCapacity> days=new LinkedHashMap<>();
        for(LocalDate d=start;!d.isAfter(end);d=d.plusDays(1)){BigDecimal h=allocated?hours.getOrDefault(d,BigDecimal.ZERO):BigDecimal.ZERO;days.put(d,new SchedulingCapacityResolver.DayCapacity(d,h.signum()>0,h,allocated));}
        return new SchedulingCapacityResolver.CapacityResolution(memberId,active,true,allocated,days);
    }
}
