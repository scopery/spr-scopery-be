package com.company.scopery.modules.workspace.invitation.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.invitation.application.command.RevokeInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RevokeInvitationAction {

    private final WorkspaceInvitationRepository invitationRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public RevokeInvitationAction(WorkspaceInvitationRepository invitationRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService,
                                   WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceInvitationResponse execute(RevokeInvitationCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), currentUserId, IamAuthorities.WORKSPACE_MANAGE_INVITATION);

        WorkspaceInvitation invitation = invitationRepository.findById(command.id())
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found: " + command.id(), null));

        WorkspaceInvitation revoked = invitation.revoke();
        WorkspaceInvitation saved = invitationRepository.save(revoked);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, saved.id(),
                WorkspaceActivityActions.REVOKE_INVITATION, "Invitation revoked");

        return WorkspaceInvitationResponse.from(saved);
    }
}
