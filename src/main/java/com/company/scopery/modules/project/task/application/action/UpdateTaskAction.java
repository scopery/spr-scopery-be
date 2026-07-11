package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.application.command.UpdateTaskCommand;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class UpdateTaskAction {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public UpdateTaskAction(TaskRepository taskRepository,
                            ProjectRepository projectRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            ProjectActivityLogger activityLogger,
                            ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public TaskResponse execute(UpdateTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireProjectPermission(task.projectId(), IamAuthorities.PROJECT_TASK_UPDATE);

        TaskStatus current = task.status();
        if (current == TaskStatus.DONE || current == TaskStatus.CANCELLED || current == TaskStatus.ARCHIVED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), "UPDATE");
        }

        if (cmd.inChargeUserId() != null && !Objects.equals(cmd.inChargeUserId(), task.inChargeUserId())) {
            Project project = projectRepository.findById(task.projectId())
                    .orElseThrow(() -> ProjectExceptions.projectNotFound(task.projectId()));
            if (!workspaceMemberRepository.isActiveMember(project.workspaceId(), cmd.inChargeUserId())) {
                throw ProjectExceptions.taskAssigneeNotWorkspaceMember(cmd.inChargeUserId());
            }
        }

        if (cmd.estimateHours() != null && cmd.estimateHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw ProjectExceptions.taskInvalidEstimate();
        }

        if (cmd.plannedStartDate() != null && cmd.dueDate() != null
                && cmd.dueDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.taskInvalidDateRange();
        }

        TaskPriority priority = ProjectEnumParser.parseOptional(
                TaskPriority.class, cmd.priority(), "TASK_INVALID_PRIORITY", "priority");
        if (priority == null) {
            priority = task.priority();
        }

        Task updated = task.update(
                cmd.title(),
                cmd.description(),
                cmd.inChargeUserId(),
                cmd.plannedRoleCode(),
                cmd.plannedRoleName(),
                cmd.estimateHours(),
                cmd.plannedStartDate(),
                cmd.dueDate(),
                priority
        );

        Task saved = taskRepository.save(updated);

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.UPDATE_TASK,
                "Task updated: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
