package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.task.application.command.ArchiveTaskCommand;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveTaskAction {

    private final TaskRepository taskRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public ArchiveTaskAction(TaskRepository taskRepository,
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
    public TaskResponse execute(ArchiveTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireTaskArchive(task.projectId());
        Project project = mutationGuard.requireMutableProject(task.projectId());

        TaskStatus current = task.status();
        if (current != TaskStatus.DONE && current != TaskStatus.CANCELLED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), TaskStatus.ARCHIVED.name());
        }

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        Task saved = taskRepository.save(task.archive(actorId));

        platformPublisher.enqueueTask(saved, "TASK_ARCHIVED");
        platformPublisher.auditTaskArchived(actorId, saved, project.organizationId(), project.workspaceId());

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.ARCHIVE_TASK,
                "Task archived: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
