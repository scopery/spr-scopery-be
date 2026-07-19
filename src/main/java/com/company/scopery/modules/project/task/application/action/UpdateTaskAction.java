package com.company.scopery.modules.project.task.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.application.command.UpdateTaskCommand;
import com.company.scopery.modules.project.task.application.response.TaskResponse;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Component
public class UpdateTaskAction {

    private final TaskRepository taskRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final WbsNodeRepository wbsNodeRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public UpdateTaskAction(TaskRepository taskRepository,
                            ProjectPhaseRepository projectPhaseRepository,
                            WbsNodeRepository wbsNodeRepository,
                            WorkspaceMemberRepository workspaceMemberRepository,
                            ProjectActivityLogger activityLogger,
                            ProjectWorkspaceAuthorizationService authorizationService,
                            ProjectMutationGuard mutationGuard,
                            ProjectPlatformPublisher platformPublisher,
                            CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.taskRepository = taskRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.wbsNodeRepository = wbsNodeRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public TaskResponse execute(UpdateTaskCommand cmd) {
        Task task = taskRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        authorizationService.requireTaskUpdate(task.projectId());
        Project project = mutationGuard.requireMutableProject(task.projectId());

        TaskStatus current = task.status();
        if (current == TaskStatus.DONE || current == TaskStatus.CANCELLED || current == TaskStatus.ARCHIVED) {
            throw ProjectExceptions.taskCannotTransition(current.name(), "UPDATE");
        }

        UUID phaseId = cmd.projectPhaseId() != null ? cmd.projectPhaseId() : task.projectPhaseId();
        UUID wbsNodeId = cmd.wbsNodeId() != null ? cmd.wbsNodeId() : task.wbsNodeId();

        if (!phaseId.equals(task.projectPhaseId())) {
            ProjectPhase phase = projectPhaseRepository.findById(phaseId)
                    .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(phaseId));
            if (!phase.projectId().equals(task.projectId())) {
                throw ProjectExceptions.taskEntityMismatch("Phase", phaseId, task.projectId());
            }
            if (phase.status() != ProjectPhaseStatus.PLANNED && phase.status() != ProjectPhaseStatus.ACTIVE) {
                throw ProjectExceptions.projectPhaseNotActive(phaseId);
            }
        }

        if (wbsNodeId != null && !Objects.equals(wbsNodeId, task.wbsNodeId())) {
            WbsNode wbs = wbsNodeRepository.findById(wbsNodeId)
                    .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(wbsNodeId));
            if (!wbs.projectId().equals(task.projectId())) {
                throw ProjectExceptions.taskEntityMismatch("WbsNode", wbsNodeId, task.projectId());
            }
            if (!wbs.projectPhaseId().equals(phaseId)) {
                throw ProjectExceptions.taskEntityMismatch("WbsNode", wbsNodeId, task.projectId());
            }
            if (wbs.status() != WbsNodeStatus.ACTIVE) {
                throw ProjectExceptions.wbsNodeAlreadyArchived(wbsNodeId);
            }
        }

        if (cmd.inChargeUserId() != null && !Objects.equals(cmd.inChargeUserId(), task.inChargeUserId())) {
            if (!workspaceMemberRepository.isActiveMember(project.workspaceId(), cmd.inChargeUserId())) {
                throw ProjectExceptions.taskAssigneeNotWorkspaceMember(cmd.inChargeUserId());
            }
        }

        BigDecimal estimateHours = cmd.estimateHours() != null ? cmd.estimateHours() : task.estimateHours();
        if (estimateHours == null) {
            throw ProjectExceptions.taskEstimateRequired();
        }
        if (estimateHours.compareTo(BigDecimal.ZERO) <= 0) {
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
                phaseId,
                wbsNodeId,
                cmd.title(),
                cmd.description(),
                cmd.inChargeUserId(),
                cmd.plannedRoleCode(),
                cmd.plannedRoleName(),
                estimateHours,
                cmd.plannedStartDate(),
                cmd.dueDate(),
                priority
        );

        boolean planningChanged = !Objects.equals(task.inChargeUserId(), updated.inChargeUserId())
                || !Objects.equals(task.estimateHours(), updated.estimateHours())
                || !Objects.equals(task.dueDate(), updated.dueDate());
        boolean assigneeChanged = !Objects.equals(task.inChargeUserId(), updated.inChargeUserId());

        Task saved = taskRepository.save(updated);

        platformPublisher.enqueueTask(saved, "TASK_UPDATED");
        if (assigneeChanged) {
            platformPublisher.enqueueTask(saved, "TASK_ASSIGNED");
        }
        if (planningChanged) {
            var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
            platformPublisher.auditTaskPlanningChanged(
                    actorId, task, saved, project.organizationId(), project.workspaceId(),
                    "Task planning fields changed");
        }

        activityLogger.logSuccess(
                ProjectEntityTypes.TASK,
                saved.id(),
                ProjectActivityActions.UPDATE_TASK,
                "Task updated: " + saved.code()
        );

        return TaskResponse.from(saved);
    }
}
