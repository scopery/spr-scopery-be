package com.company.scopery.modules.project.gantt.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.project.gantt.application.command.CreateGanttDependencyCommand;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectErrorCatalog;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.taskdependency.application.action.CreateTaskDependencyAction;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGanttDependencyActionTest {

    @Mock ProjectWorkspaceAuthorizationService authorization;
    @Mock CreateTaskDependencyAction createTaskDependencyAction;
    @Mock ProjectRepository projects;
    @Mock ProjectPlatformPublisher publisher;
    @Mock ProjectActivityLogger activityLogger;
    @Mock RecalculateGanttAction recalculateGanttAction;

    CreateGanttDependencyAction action;
    UUID projectId = UUID.randomUUID();
    UUID pred = UUID.randomUUID();
    UUID succ = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        action = new CreateGanttDependencyAction(authorization, createTaskDependencyAction, projects,
                publisher, activityLogger, recalculateGanttAction);
    }

    @Test
    void wrapsCreateTaskDependency() {
        TaskDependencyResponse response = mock(TaskDependencyResponse.class);
        when(response.id()).thenReturn(UUID.randomUUID());
        when(createTaskDependencyAction.execute(any())).thenReturn(response);

        TaskDependencyResponse result = action.execute(new CreateGanttDependencyCommand(
                projectId, pred, succ, "FINISH_TO_START", 0, false));

        assertThat(result).isSameAs(response);
        verify(publisher).enqueueGantt(eq(projectId), eq("GANTT_DEPENDENCY_CREATED"), any());
        verify(recalculateGanttAction, never()).execute(any());
    }

    @Test
    void reusesSelfDependencyException() {
        when(createTaskDependencyAction.execute(any()))
                .thenThrow(new AppException(ProjectErrorCatalog.TASK_DEPENDENCY_SELF_REFERENCE));

        assertThatThrownBy(() -> action.execute(new CreateGanttDependencyCommand(
                projectId, pred, pred, "FINISH_TO_START", 0, false)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(ProjectErrorCatalog.TASK_DEPENDENCY_SELF_REFERENCE.code()));
    }
}
