package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.task.application.command.StartTaskCommand;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StartTaskAction {

    private final TaskRepository taskRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public StartTaskAction(TaskRepository taskRepository,
                           ProjectActivityLogger activityLogger,
                           ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public TaskResponse execute(StartTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireProjectPermission(task.projectId(), IamAuthorities.PROJECT_TASK_UPDATE);

        TaskStatus current = task.status();
        if (current != TaskStatus.TODO && current != TaskStatus.BLOCKED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), TaskStatus.IN_PROGRESS.name());
        }

        Task saved = taskRepository.save(task.start());

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.START_TASK,
                "Task started: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
