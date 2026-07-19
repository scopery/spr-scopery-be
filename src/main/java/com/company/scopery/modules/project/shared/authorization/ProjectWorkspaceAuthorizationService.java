package com.company.scopery.modules.project.shared.authorization;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Centralized workspace-scoped IAM checks for Project Core.
 *
 * <p>Permission simplification (Phase 10 intentional mapping):
 * <ul>
 *   <li>{@code PROJECT_ACTIVATE} → {@link IamAuthorities#PROJECT_UPDATE}</li>
 *   <li>{@code PROJECT_PHASE_ACTIVATE} / {@code PROJECT_PHASE_COMPLETE} → {@link IamAuthorities#PROJECT_PHASE_UPDATE}</li>
 *   <li>{@code PROJECT_WBS_MOVE} → {@link IamAuthorities#PROJECT_WBS_UPDATE}</li>
 *   <li>{@code PROJECT_TASK_ASSIGN} / {@code PROJECT_TASK_STATUS_UPDATE} → {@link IamAuthorities#PROJECT_TASK_UPDATE}</li>
 *   <li>{@code PROJECT_TASK_DEPENDENCY_*} → view uses {@link IamAuthorities#PROJECT_TASK_VIEW}; create/remove use {@link IamAuthorities#PROJECT_TASK_UPDATE}</li>
 * </ul>
 *
 * <p>Access denial surfaces as {@code IAM_ACCESS_DENIED} (403). Missing projects return 404.
 * List endpoints require an explicit workspace filter + {@code PROJECT_VIEW}.
 */
@Component
public class ProjectWorkspaceAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ProjectRepository projectRepository;

    public ProjectWorkspaceAuthorizationService(CurrentUserAuthorizationService currentUserService,
                                                   WorkspaceIamIntegrationService iamIntegrationService,
                                                   ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.projectRepository = projectRepository;
    }

    public void requireWorkspacePermission(UUID workspaceId, IamPermissionAction authority) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, userId, authority);
    }

    public void requireProjectPermission(UUID projectId, IamPermissionAction authority) {
        UUID workspaceId = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId))
                .workspaceId();
        requireWorkspacePermission(workspaceId, authority);
    }

    // ── Project ───────────────────────────────────────────────────────────────

    public void requireProjectCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_CREATE);
    }

    public void requireProjectView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_VIEW);
    }

    public void requireProjectViewByProjectId(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_VIEW);
    }

    public void requireProjectUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_UPDATE);
    }

    /** Simplified: PROJECT_ACTIVATE → PROJECT_UPDATE */
    public void requireProjectActivate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_UPDATE);
    }

    public void requireProjectArchive(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_ARCHIVE);
    }

    // ── ProjectPhase ──────────────────────────────────────────────────────────

    public void requireProjectPhaseView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_VIEW);
    }

    public void requireProjectPhaseCreate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_CREATE);
    }

    public void requireProjectPhaseUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_UPDATE);
    }

    /** Simplified: PROJECT_PHASE_ACTIVATE → PROJECT_PHASE_UPDATE */
    public void requireProjectPhaseActivate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_UPDATE);
    }

    /** Simplified: PROJECT_PHASE_COMPLETE → PROJECT_PHASE_UPDATE */
    public void requireProjectPhaseComplete(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_UPDATE);
    }

    public void requireProjectPhaseArchive(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_PHASE_ARCHIVE);
    }

    // ── WBS ───────────────────────────────────────────────────────────────────

    public void requireWbsView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_VIEW);
    }

    public void requireWbsCreate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_CREATE);
    }

    public void requireWbsUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_UPDATE);
    }

    /** Simplified: PROJECT_WBS_MOVE → PROJECT_WBS_UPDATE */
    public void requireWbsMove(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_UPDATE);
    }

    public void requireWbsArchive(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_ARCHIVE);
    }

    // ── Task ──────────────────────────────────────────────────────────────────

    public void requireTaskView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_VIEW);
    }

    public void requireTaskCreate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_CREATE);
    }

    public void requireTaskUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /** Simplified: PROJECT_TASK_ASSIGN → PROJECT_TASK_UPDATE */
    public void requireTaskAssign(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /** Simplified: PROJECT_TASK_STATUS_UPDATE → PROJECT_TASK_UPDATE */
    public void requireTaskStatusUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireTaskArchive(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_ARCHIVE);
    }

    // ── Task dependency ───────────────────────────────────────────────────────

    /** Simplified: PROJECT_TASK_DEPENDENCY_VIEW → PROJECT_TASK_VIEW */
    public void requireTaskDependencyView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_VIEW);
    }

    /** Simplified: PROJECT_TASK_DEPENDENCY_CREATE → PROJECT_TASK_UPDATE */
    public void requireTaskDependencyCreate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /** Simplified: PROJECT_TASK_DEPENDENCY_REMOVE → PROJECT_TASK_UPDATE */
    public void requireTaskDependencyRemove(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /** Phase 13 simplification: TASK_SCHEDULE_VIEW / VIEW_ISSUES → PROJECT_TASK_VIEW. */
    public void requireScheduleView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_VIEW);
    }

    /** Phase 13 simplification: TASK_SCHEDULE_RECALCULATE → PROJECT_TASK_UPDATE. */
    public void requireScheduleRecalculate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /** Phase 13 simplification: TASK_SCHEDULE_CANCEL_RUN → PROJECT_TASK_UPDATE. */
    public void requireScheduleCancel(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    /**
     * Phase 14 Gantt auth simplification (no new IAM rights):
     * <ul>
     *   <li>Gantt view → {@link IamAuthorities#PROJECT_TASK_VIEW}</li>
     *   <li>Gantt recalculate / move / resize / clear-override / deps / milestones mutate
     *       → {@link IamAuthorities#PROJECT_TASK_UPDATE}</li>
     * </ul>
     */
    public void requireGanttView(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_VIEW);
    }

    public void requireGanttRecalculate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttMoveTask(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttResizeTask(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttClearOverride(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttEditDependency(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttMilestoneCreate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttMilestoneUpdate(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    public void requireGanttMilestoneArchive(UUID projectId) {
        requireProjectPermission(projectId, IamAuthorities.PROJECT_TASK_UPDATE);
    }

    // ── Project template ──────────────────────────────────────────────────────

    public void requireTemplateView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_VIEW);
    }

    public void requireTemplateCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_CREATE);
    }

    public void requireTemplateUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_UPDATE);
    }

    /** Phase 11: publish maps to UPDATE action / PUBLISH_PROJECT_TEMPLATE right. */
    public void requireTemplatePublish(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_PUBLISH);
    }

    public void requireTemplateArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_ARCHIVE);
    }

    public void requireTemplateApply(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.PROJECT_TEMPLATE_APPLY);
    }
}
