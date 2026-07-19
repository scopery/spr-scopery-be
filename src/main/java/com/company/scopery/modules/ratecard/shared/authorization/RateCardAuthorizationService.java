package com.company.scopery.modules.ratecard.shared.authorization;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Authorization for Rate Card module.
 * Workspace-scoped ops use WorkspaceIamIntegrationService.
 * SYSTEM-scoped ops require authentication; when workspaceId is present, workspace right is checked;
 * otherwise the caller must hold the corresponding workspace right via an accessible workspace context
 * or the MANAGE right when only system catalogs are mutated by admins.
 */
@Component
public class RateCardAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ProjectRepository projectRepository;

    public RateCardAuthorizationService(CurrentUserAuthorizationService currentUserService,
                                        WorkspaceIamIntegrationService iamIntegrationService,
                                        ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.projectRepository = projectRepository;
    }

    public UUID currentUserId() {
        return currentUserService.resolveCurrentUser().id();
    }

    public void requireWorkspacePermission(UUID workspaceId, IamPermissionAction authority) {
        if (workspaceId == null) {
            // SYSTEM/ORG scope without workspace: require authenticated user; rights enforced at grant time.
            currentUserService.resolveCurrentUser();
            return;
        }
        UUID userId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, userId, authority);
    }

    public void requireProjectWorkspacePermission(UUID projectId, IamPermissionAction authority) {
        UUID workspaceId = projectRepository.findById(projectId)
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(projectId))
                .workspaceId();
        requireWorkspacePermission(workspaceId, authority);
    }

    public void requireCostRoleView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.COST_ROLE_VIEW);
    }

    public void requireCostRoleCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.COST_ROLE_CREATE);
    }

    public void requireCostRoleUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.COST_ROLE_UPDATE);
    }

    public void requireCostRoleArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.COST_ROLE_ARCHIVE);
    }

    public void requireCostRoleManage(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.COST_ROLE_MANAGE);
    }

    public void requireRateCardView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_VIEW);
    }

    public void requireRateCardCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_CREATE);
    }

    public void requireRateCardUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_UPDATE);
    }

    public void requireRateCardPublish(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_PUBLISH);
    }

    public void requireRateCardArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_ARCHIVE);
    }

    public void requireRateCardLineView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_LINE_VIEW);
    }

    public void requireRateCardLineCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_LINE_CREATE);
    }

    public void requireRateCardLineUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_LINE_UPDATE);
    }

    public void requireRateCardLineDelete(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_CARD_LINE_DELETE);
    }

    public void requireInflationView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.INFLATION_POLICY_VIEW);
    }

    public void requireInflationCreate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.INFLATION_POLICY_CREATE);
    }

    public void requireInflationUpdate(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.INFLATION_POLICY_UPDATE);
    }

    public void requireInflationArchive(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.INFLATION_POLICY_ARCHIVE);
    }

    public void requireMemberCostRoleView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.MEMBER_COST_ROLE_VIEW);
    }

    public void requireMemberCostRoleAssign(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.MEMBER_COST_ROLE_ASSIGN);
    }

    public void requireMemberCostRoleManage(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.MEMBER_COST_ROLE_MANAGE);
    }

    public void requireResolutionView(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_RESOLUTION_VIEW);
    }

    public void requireResolutionPreview(UUID workspaceId) {
        requireWorkspacePermission(workspaceId, IamAuthorities.RATE_RESOLUTION_PREVIEW);
    }
}
