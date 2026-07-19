package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.project.gantt.application.command.ResizeGanttTaskCommand;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
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
class ResizeGanttTaskActionTest {

    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock ProjectMutationGuard mutationGuard;
    @Mock TaskRepository tasks;
    @Mock TaskScheduleOverrideRepository overrides;
    @Mock ProjectPlatformPublisher publisher;
    @Mock ProjectActivityLogger activityLogger;
    @Mock CurrentUserAuthorizationService currentUser;
    @Mock RecalculateGanttAction recalculateGanttAction;

    ResizeGanttTaskAction action;
    UUID projectId = UUID.randomUUID();
    UUID taskId = UUID.randomUUID();
    UUID actorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new ResizeGanttTaskAction(authorization, mutationGuard, tasks, overrides, publisher,
                activityLogger, currentUser, recalculateGanttAction);
    }

    @Test
    void createsPinFinishOverrideWithoutChangingEstimateHours() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        Task task = task();
        when(tasks.findById(taskId)).thenReturn(Optional.of(task));
        when(currentUser.resolveCurrentUser()).thenReturn(user());
        when(overrides.findActiveByTaskId(taskId)).thenReturn(Optional.empty());
        when(overrides.save(any())).thenAnswer(i -> i.getArgument(0));

        action.execute(new ResizeGanttTaskCommand(
                projectId, taskId, LocalDate.of(2026, 8, 12), "Extended timeline", false));

        ArgumentCaptor<TaskScheduleOverride> captor = ArgumentCaptor.forClass(TaskScheduleOverride.class);
        verify(overrides).save(captor.capture());
        assertThat(captor.getValue().overrideType()).isEqualTo(ScheduleOverrideType.PIN_FINISH);
        assertThat(captor.getValue().manualFinishDate()).isEqualTo(LocalDate.of(2026, 8, 12));
        verify(tasks, never()).save(any());
    }

    @Test
    void reasonRequired() {
        when(mutationGuard.requireMutableProject(projectId)).thenReturn(project());
        assertThatThrownBy(() -> action.execute(new ResizeGanttTaskCommand(
                projectId, taskId, LocalDate.of(2026, 8, 12), "", false)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.GANTT_TASK_OVERRIDE_REASON_REQUIRED.code()));
    }

    private Project project() {
        return new Project(projectId, UUID.randomUUID(), UUID.randomUUID(), "P", "Project", null,
                actorId, "USD", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31),
                ProjectStatus.ACTIVE, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null);
    }

    private Task task() {
        Task t = Task.create(projectId, null, null, "T1", "Task", null, actorId, null, null,
                new BigDecimal("8"), null, null, TaskPriority.MEDIUM);
        return new Task(taskId, t.projectId(), t.projectPhaseId(), t.wbsNodeId(), t.code(), t.title(),
                t.description(), t.inChargeUserId(), t.plannedRoleCode(), t.plannedRoleName(),
                t.estimateHours(), t.plannedStartDate(), t.dueDate(), t.priority(), t.status(),
                t.startedAt(), t.startedBy(), t.blockedAt(), t.completedAt(), t.completedBy(),
                t.cancelledAt(), t.cancelledBy(), t.archivedAt(), t.archivedBy(), t.version(),
                t.createdAt(), t.updatedAt());
    }

    private IamUser user() {
        return IamUser.create(
                com.company.scopery.modules.iam.user.domain.valueobject.Username.of("actor"),
                com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress.of("actor@example.com"),
                "Actor");
    }
}
