package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
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

    public ArchiveTaskAction(TaskRepository taskRepository,
                             ProjectActivityLogger activityLogger,
                             ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public TaskResponse execute(ArchiveTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireProjectPermission(task.projectId(), IamAuthorities.PROJECT_TASK_ARCHIVE);

        TaskStatus current = task.status();
        if (current != TaskStatus.DONE && current != TaskStatus.CANCELLED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), TaskStatus.ARCHIVED.name());
        }

        Task saved = taskRepository.save(task.archive());

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.ARCHIVE_TASK,
                "Task archived: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
