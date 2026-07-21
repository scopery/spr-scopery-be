package com.company.scopery.modules.project.gantt.application.service;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.project.gantt.application.query.GanttViewQuery;
import com.company.scopery.modules.project.gantt.application.response.GanttViewResponse;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRun;
import com.company.scopery.modules.project.scheduling.schedulerun.domain.model.ScheduleRunRepository;
import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssueRepository;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleRiskStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.enums.TaskScheduleStatus;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskSchedule;
import com.company.scopery.modules.project.scheduling.taskschedule.domain.model.TaskScheduleRepository;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GanttQueryServiceTest {

    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock ProjectRepository projects;
    @Mock ProjectPhaseRepository phases;
    @Mock WbsNodeRepository wbsNodes;
    @Mock TaskRepository tasks;
    @Mock TaskDependencyRepository dependencies;
    @Mock ScheduleRunRepository runs;
    @Mock TaskScheduleRepository schedules;
    @Mock TaskScheduleOverrideRepository overrides;
    @Mock SchedulingIssueRepository issues;
    @Mock ProjectMilestoneRepository milestones;

    GanttQueryService service;
    UUID projectId = UUID.randomUUID();
    UUID workspaceId = UUID.randomUUID();
    UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GanttQueryService(authorization, projects, phases, wbsNodes, tasks, dependencies,
                runs, schedules, overrides, issues, milestones);
    }

    @Test
    void projectionSpansScheduledAndUnscheduledTasks() {
        Project project = project();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        ProjectPhase phase = ProjectPhase.create(projectId, "P1", "Phase 1", null, 1,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));
        when(phases.findAllByProjectId(projectId)).thenReturn(List.of(phase));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(List.of());
        Task scheduled = Task.create(projectId, phase.id(), null, "T1", "Scheduled", null,
                UUID.randomUUID(), null, null, new BigDecimal("4"), LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5), TaskPriority.MEDIUM);
        Task unscheduled = Task.create(projectId, phase.id(), null, "T2", "Unscheduled", null,
                null, null, null, null, null, null, TaskPriority.LOW);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(scheduled, unscheduled));
        when(milestones.findAllByProjectId(projectId)).thenReturn(List.of());
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of());

        ScheduleRun run = ScheduleRun.create(projectId, workspaceId,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), "{}", null, null)
                .completed("{}");
        when(runs.findAllByProjectId(projectId)).thenReturn(List.of(run));
        when(schedules.findAllByScheduleRunId(run.id())).thenReturn(List.of(
                TaskSchedule.create(run.id(), projectId, scheduled.id(), scheduled.inChargeUserId(), null,
                        LocalDate.of(2026, 8, 2), LocalDate.of(2026, 8, 3),
                        new BigDecimal("4"), BigDecimal.ZERO, scheduled.dueDate(), BigDecimal.ZERO,
                        TaskScheduleRiskStatus.ON_TRACK, TaskScheduleStatus.SCHEDULED)));
        when(issues.findAllByScheduleRunId(run.id())).thenReturn(List.of());
        when(overrides.findActiveByProjectId(projectId)).thenReturn(List.of());

        GanttViewResponse view = service.getView(new GanttViewQuery(
                projectId, null, null, null, true, false, "PHASE"));

        assertThat(view.items()).extracting(i -> i.itemType()).contains("PROJECT", "PHASE", "TASK");
        assertThat(view.items()).filteredOn(i -> "TASK".equals(i.itemType()))
                .extracting(i -> i.scheduleStatus())
                .contains("SCHEDULED", "UNSCHEDULED");
        assertThat(view.summary().unscheduledTaskCount()).isGreaterThanOrEqualTo(1);
        assertThat(view.items()).allSatisfy(item -> {
            if (item.metadata() != null) {
                assertThat(item.metadata().keySet()).noneMatch(k ->
                        k.toLowerCase().contains("cost")
                                || k.toLowerCase().contains("quote")
                                || k.toLowerCase().contains("finance")
                                || k.toLowerCase().contains("baseline"));
            }
        });
    }

    @Test
    void activeOverrideDatesAppearInProjectionWithoutRecalculate() {
        Project project = project();
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        ProjectPhase phase = ProjectPhase.create(projectId, "P1", "Phase 1", null, 1,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));
        when(phases.findAllByProjectId(projectId)).thenReturn(List.of(phase));
        when(wbsNodes.findAllByProjectId(projectId)).thenReturn(List.of());
        Task scheduled = Task.create(projectId, phase.id(), null, "T1", "Scheduled", null,
                UUID.randomUUID(), null, null, new BigDecimal("4"), LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5), TaskPriority.MEDIUM);
        when(tasks.findAllByProjectId(projectId)).thenReturn(List.of(scheduled));
        when(milestones.findAllByProjectId(projectId)).thenReturn(List.of());
        when(dependencies.findActiveByProjectId(projectId)).thenReturn(List.of());

        ScheduleRun run = ScheduleRun.create(projectId, workspaceId,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31), "{}", null, null)
                .completed("{}");
        when(runs.findAllByProjectId(projectId)).thenReturn(List.of(run));
        when(schedules.findAllByScheduleRunId(run.id())).thenReturn(List.of(
                TaskSchedule.create(run.id(), projectId, scheduled.id(), scheduled.inChargeUserId(), null,
                        LocalDate.of(2026, 8, 2), LocalDate.of(2026, 8, 3),
                        new BigDecimal("4"), BigDecimal.ZERO, scheduled.dueDate(), BigDecimal.ZERO,
                        TaskScheduleRiskStatus.ON_TRACK, TaskScheduleStatus.SCHEDULED)));
        when(issues.findAllByScheduleRunId(run.id())).thenReturn(List.of());
        when(overrides.findActiveByProjectId(projectId)).thenReturn(List.of(
                TaskScheduleOverride.create(projectId, scheduled.id(), ScheduleOverrideType.PIN_RANGE,
                        LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10), null, "drag")));

        GanttViewResponse view = service.getView(new GanttViewQuery(
                projectId, null, null, null, true, false, "PHASE"));

        assertThat(view.items()).filteredOn(i -> "TASK".equals(i.itemType()))
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.startDate()).isEqualTo(LocalDate.of(2026, 9, 1));
                    assertThat(item.endDate()).isEqualTo(LocalDate.of(2026, 9, 10));
                    assertThat(item.metadata()).containsEntry("hasManualOverride", true);
                });
    }

    @Test
    void scheduleRunMismatchRejected() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project()));
        UUID otherProjectRun = UUID.randomUUID();
        ScheduleRun foreign = ScheduleRun.create(UUID.randomUUID(), workspaceId,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10), "{}", null, null);
        when(runs.findById(otherProjectRun)).thenReturn(Optional.of(foreign));

        assertThatThrownBy(() -> service.getView(new GanttViewQuery(
                projectId, otherProjectRun, null, null, true, false, "PHASE")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.GANTT_SCHEDULE_RUN_PROJECT_MISMATCH.code()));
    }

    private Project project() {
        return new Project(projectId, workspaceId, orgId, "PRJ", "Project", null,
                UUID.randomUUID(), "USD", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31),
                ProjectStatus.ACTIVE, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null);
    }
}
