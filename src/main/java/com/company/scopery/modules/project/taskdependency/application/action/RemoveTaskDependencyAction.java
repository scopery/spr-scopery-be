package com.company.scopery.modules.project.taskdependency.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
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

    public RemoveTaskDependencyAction(TaskDependencyRepository taskDependencyRepository,
                                      ProjectActivityLogger activityLogger,
                                      ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskDependencyRepository = taskDependencyRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public TaskDependencyResponse execute(RemoveTaskDependencyCommand cmd) {
        TaskDependency dep = taskDependencyRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskDependencyNotFound(cmd.id()));

        if (!dep.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskDependencyProjectMismatch(dep.id(), cmd.projectId());
        }

        authorizationService.requireProjectPermission(dep.projectId(), IamAuthorities.PROJECT_TASK_UPDATE);

        taskDependencyRepository.deleteById(dep.id());

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK_DEPENDENCY,
                dep.id(),
                ProjectActivityActions.REMOVE_TASK_DEPENDENCY,
                "Task dependency removed: " + dep.predecessorTaskId() + " -> " + dep.successorTaskId());

        return TaskDependencyResponse.from(dep);
    }
}
