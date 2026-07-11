package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CancelOrgInvitationAction {

    private final OrgInvitationRepository invitationRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public CancelOrgInvitationAction(OrgInvitationRepository invitationRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService iamIntegrationService,
                                      WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgInvitationResponse execute(UUID organizationId, UUID invitationId) {
        OrgInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> WorkspaceExceptions.orgInvitationNotFound(invitationId));

        if (!invitation.organizationId().equals(organizationId)) {
            throw WorkspaceExceptions.orgInvitationNotFound(invitationId);
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(organizationId, actorId, IamAuthorities.ORGANIZATION_MANAGE);

        if (invitation.status() != OrgInvitationStatus.PENDING) {
            throw WorkspaceExceptions.orgInvitationNotPending(invitationId);
        }

        OrgInvitation cancelled = invitation.cancel();
        OrgInvitation saved = invitationRepository.save(cancelled);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_INVITATION, saved.id(),
                WorkspaceActivityActions.CANCEL_ORG_INVITATION,
                "Org invitation cancelled: " + saved.inviteeEmail());

        return OrgInvitationResponse.from(saved);
    }
}
