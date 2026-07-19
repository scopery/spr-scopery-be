package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.gantt.application.command.MoveGanttTaskCommand;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.scheduleoverride.application.response.TaskScheduleOverrideResponse;
import com.company.scopery.modules.project.scheduleoverride.domain.enums.ScheduleOverrideType;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverride;
import com.company.scopery.modules.project.scheduleoverride.domain.model.TaskScheduleOverrideRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveGanttTaskActionTest {

    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock ProjectMutationGuard mutationGuard;
    @Mock TaskRepository tasks;
    @Mock TaskScheduleOverrideRepository overrides;
    @Mock ProjectPlatformPublisher publisher;
    @Mock ProjectActivityLogger activityLogger;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock RecalculateGanttAction recalculateGanttAction;

    MoveGanttTaskAction action;
    UUID projectId = UUID.randomUUID();
    UUID taskId = UUID.randomUUID();
    UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new MoveGanttTaskAction(authorization, mutationGuard, tasks, overrides, publisher,
                activityLogger, currentUser, recalculateGanttAction);
    }

    @Test
    void createsPinRangeOverrideWithoutChangingEstimateHours() {
        Project project = project();
        Task task = Task.create(projectId, null, null, "T1", "Task", null, actorId, null, null,
                new BigDecimal("8"), LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5), TaskPriority.MEDIUM);
        task = new Task(taskId, task.projectId(), task.projectPhaseId(), task.wbsNodeId(), task.code(),
                task.title(), task.description(), task.inChargeUserId(), task.plannedRoleCode(),
                task.plannedRoleName(), task.estimateHours(), task.plannedStartDate(), task.dueDate(),
                task.priority(), task.status(), task.startedAt(), task.startedBy(), task.blockedAt(),
                task.completedAt(), task.completedBy(), task.cancelledAt(), task.cancelledBy(),
                task.archivedAt(), task.archivedBy(), task.version(), task.createdAt(), task.updatedAt());

        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project);
        when(tasks.findById(taskId)).thenReturn(Optional.of(task));
        when(currentUser.resolveCurrentUser()).thenReturn(mockUser());
        when(overrides.findActiveByTaskId(taskId)).thenReturn(Optional.empty());
        when(overrides.save(any())).thenAnswer(i -> i.getArgument(0));

        BigDecimal estimateBefore = task.estimateHours();
        TaskScheduleOverrideResponse response = action.execute(new MoveGanttTaskCommand(
                projectId, taskId, LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 9),
                "Client dependency", false));

        ArgumentCaptor<TaskScheduleOverride> captor = ArgumentCaptor.forClass(TaskScheduleOverride.class);
        verify(overrides).save(captor.capture());
        assertThat(captor.getValue().overrideType()).isEqualTo(ScheduleOverrideType.PIN_RANGE);
        assertThat(response.overrideType()).isEqualTo("PIN_RANGE");
        assertThat(estimateBefore).isEqualByComparingTo(new BigDecimal("8"));
        verify(tasks, never()).save(any());
        verify(recalculateGanttAction, never()).execute(any());
    }

    @Test
    void invalidRangeRejected() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        assertThatThrownBy(() -> action.execute(new MoveGanttTaskCommand(
                projectId, taskId, LocalDate.of(2026, 8, 9), LocalDate.of(2026, 8, 5), "reason", false)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_INVALID_DATE_RANGE.code()));
    }

    @Test
    void reasonRequired() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        assertThatThrownBy(() -> action.execute(new MoveGanttTaskCommand(
                projectId, taskId, LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 9), "  ", false)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_REASON_REQUIRED.code()));
    }

    private Project project() {
        return new Project(projectId, UUID.randomUUID(), UUID.randomUUID(), "P", "Project", null,
                actorId, "USD", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31),
                ProjectStatus.ACTIVE, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null);
    }

    private IamUser mockUser() {
        return IamUser.create(
                com.company.scopery.modules.iam.user.domain.valueobject.Username.of("actor"),
                com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress.of("actor@example.com"),
                "Actor");
    }
}
