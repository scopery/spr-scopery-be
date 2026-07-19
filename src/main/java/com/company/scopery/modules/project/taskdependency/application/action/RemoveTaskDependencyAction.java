package com.company.scopery.modules.project.taskdependency.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.taskdependency.application.command.RemoveTaskDependencyCommand;
import com.company.scopery.modules.project.taskdependency.application.response.TaskDependencyResponse;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveTaskDependencyAction {

    private final TaskDependencyRepository taskDependencyRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public RemoveTaskDependencyAction(TaskDependencyRepository taskDependencyRepository,
                                      ProjectActivityLogger activityLogger,
                                      ProjectWorkspaceAuthorizationService authorizationService,
                                      ProjectMutationGuard mutationGuard,
                                      ProjectPlatformPublisher platformPublisher,
                                      CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.taskDependencyRepository = taskDependencyRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public TaskDependencyResponse execute(RemoveTaskDependencyCommand cmd) {
        TaskDependency dep = taskDependencyRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskDependencyNotFound(cmd.id()));

        if (!dep.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskDependencyProjectMismatch(dep.id(), cmd.projectId());
        }

        authorizationService.requireTaskDependencyRemove(dep.projectId());
        Project project = mutationGuard.requireMutableProject(dep.projectId());

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();

        taskDependencyRepository.deleteById(dep.id());

        platformPublisher.enqueueDependency(dep, "TASK_DEPENDENCY_REMOVED");
        platformPublisher.auditDependencyRemoved(actorId, dep, project.organizationId(), project.workspaceId());

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK_DEPENDENCY,
                dep.id(),
                ProjectActivityActions.REMOVE_TASK_DEPENDENCY,
                "Task dependency removed: " + dep.predecessorTaskId() + " -> " + dep.successorTaskId());

        return TaskDependencyResponse.from(dep);
    }
}
