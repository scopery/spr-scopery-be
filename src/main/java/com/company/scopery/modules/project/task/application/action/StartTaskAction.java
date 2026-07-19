package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
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
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public StartTaskAction(TaskRepository taskRepository,
                           ProjectActivityLogger activityLogger,
                           ProjectWorkspaceAuthorizationService authorizationService,
                           ProjectMutationGuard mutationGuard,
                           ProjectPlatformPublisher platformPublisher,
                           CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.taskRepository = taskRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public TaskResponse execute(StartTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireTaskStatusUpdate(task.projectId());
        mutationGuard.requireMutableProject(task.projectId());

        TaskStatus current = task.status();
        if (current != TaskStatus.TODO && current != TaskStatus.BLOCKED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), TaskStatus.IN_PROGRESS.name());
        }

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        Task saved = taskRepository.save(task.start(actorId));

        platformPublisher.enqueueTask(saved, "TASK_STARTED");

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.START_TASK,
                "Task started: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
