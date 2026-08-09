package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.task.application.command.DeleteTaskCommand;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteTaskAction {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public DeleteTaskAction(TaskRepository taskRepository,
                            TaskDependencyRepository dependencyRepository,
                            ProjectWorkspaceAuthorizationService authorizationService,
                            ProjectMutationGuard mutationGuard,
                            ProjectActivityLogger activityLogger,
                            CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.activityLogger = activityLogger;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public void execute(DeleteTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireTaskArchive(task.projectId());
        mutationGuard.requireMutableProject(task.projectId());

        if (task.status() != TaskStatus.TODO) {
            throw ProjectExceptions.taskCannotDelete(task.id());
        }

        // Cascade: remove all dependency edges involving this task
        dependencyRepository.deleteAllByTaskId(task.id());

        taskRepository.deleteById(task.id());

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                task.id(),
                ProjectActivityActions.ARCHIVE_TASK,
                "Task deleted: " + task.code()
        );
    }
}
