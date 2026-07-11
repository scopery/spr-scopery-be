package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.application.command.CreateTaskCommand;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateTaskAction {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final WbsNodeRepository wbsNodeRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public CreateTaskAction(TaskRepository taskRepository,
                            ProjectRepository projectRepository,
                            ProjectPhaseRepository projectPhaseRepository,
                            WbsNodeRepository wbsNodeRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            ProjectActivityLogger activityLogger,
                            ProjectWorkspaceAuthorizationService authorizationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.wbsNodeRepository = wbsNodeRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public TaskResponse execute(CreateTaskCommand cmd) {
        authorizationService.requireProjectPermission(cmd.projectId(), IamAuthorities.PROJECT_TASK_CREATE);

        Project project = projectRepository.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));

        if (project.status() != ProjectStatus.DRAFT && project.status() != ProjectStatus.ACTIVE) {
            throw ProjectExceptions.projectNotActiveOrDraft(cmd.projectId());
        }

        ProjectPhase phase = projectPhaseRepository.findById(cmd.projectPhaseId())
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.projectPhaseId()));

        if (!phase.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskEntityMismatch("Phase", cmd.projectPhaseId(), cmd.projectId());
        }

        if (phase.status() != ProjectPhaseStatus.PLANNED && phase.status() != ProjectPhaseStatus.ACTIVE) {
            throw ProjectExceptions.projectPhaseNotActive(cmd.projectPhaseId());
        }

        WbsNode wbs = wbsNodeRepository.findById(cmd.wbsNodeId())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.wbsNodeId()));

        if (!wbs.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskEntityMismatch("WbsNode", cmd.wbsNodeId(), cmd.projectId());
        }

        if (!wbs.projectPhaseId().equals(cmd.projectPhaseId())) {
            throw ProjectExceptions.taskEntityMismatch("WbsNode", cmd.wbsNodeId(), cmd.projectId());
        }

        if (wbs.status() != WbsNodeStatus.ACTIVE) {
            throw ProjectExceptions.wbsNodeAlreadyArchived(cmd.wbsNodeId());
        }

        if (cmd.plannedStartDate() != null && cmd.dueDate() != null
                && cmd.dueDate().isBefore(cmd.plannedStartDate())) {
            throw ProjectExceptions.taskInvalidDateRange();
        }

        if (taskRepository.existsByProjectIdAndCode(cmd.projectId(), cmd.code())) {
            throw ProjectExceptions.taskCodeAlreadyExists(cmd.code(), cmd.projectId());
        }

        if (cmd.inChargeUserId() != null
                && !workspaceMemberRepository.isActiveMember(project.workspaceId(), cmd.inChargeUserId())) {
            throw ProjectExceptions.taskAssigneeNotWorkspaceMember(cmd.inChargeUserId());
        }

        if (cmd.estimateHours() != null && cmd.estimateHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw ProjectExceptions.taskInvalidEstimate();
        }

        TaskPriority priority = ProjectEnumParser.parseOptional(
                TaskPriority.class, cmd.priority(), "TASK_INVALID_PRIORITY", "priority");
        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }

        Task task = Task.create(
                cmd.projectId(),
                cmd.projectPhaseId(),
                cmd.wbsNodeId(),
                cmd.code(),
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

        Task saved = taskRepository.save(task);

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.CREATE_TASK,
                "Task created: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
