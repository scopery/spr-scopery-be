package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.orginvitation.application.command.AcceptOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMembershipEnrollmentService;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AcceptOrgInvitationAction {

    private final OrgInvitationRepository invitationRepository;
    private final OrgMembershipEnrollmentService enrollmentService;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceActivityLogger activityLogger;
    private final InvitationInboxCleanupService inboxCleanupService;
    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public AcceptOrgInvitationAction(OrgInvitationRepository invitationRepository,
                                      OrgMembershipEnrollmentService enrollmentService,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceActivityLogger activityLogger,
                                      InvitationInboxCleanupService inboxCleanupService,
                                      InAppDeliveryService inAppDeliveryService,
                                      WorkspaceAudienceResolver audienceResolver) {
        this.invitationRepository = invitationRepository;
        this.enrollmentService = enrollmentService;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.inboxCleanupService = inboxCleanupService;
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional
    public OrgInvitationResponse execute(AcceptOrgInvitationCommand command) {
        String tokenHash = InvitationCodeHasher.hash(command.token());
        OrgInvitation invitation = invitationRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> WorkspaceExceptions.orgInvitationNotFound(command.token()));

        if (invitation.status() != OrgInvitationStatus.PENDING) {
            throw WorkspaceExceptions.orgInvitationNotPending(invitation.id());
        }
        if (invitation.isExpired()) {
            throw WorkspaceExceptions.orgInvitationExpired(invitation.id());
        }

        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        var currentUser = currentUserAuthorizationService.resolveCurrentUser();
        if (currentUser.email() == null
                || currentUser.email().value() == null
                || !currentUser.email().value().equalsIgnoreCase(invitation.inviteeEmail())) {
            throw WorkspaceExceptions.orgInvitationNotFound(command.token());
        }

        enrollmentService.ensureActiveMembership(
                invitation.organizationId(),
                userId,
                invitation.membershipType(),
                OrgMembershipSource.ORGANIZATION_INVITATION,
                WorkspaceExceptions::orgInvitationAlreadyMember);

        OrgInvitation accepted = invitation.accept(userId);
        OrgInvitation saved = invitationRepository.save(accepted);

        inboxCleanupService.dismissOrgInvitation(saved.id(), userId);

        if (saved.invitedBy() != null) {
            inAppDeliveryService.deliverNotificationFyi(
                    audienceResolver.explicitUser(saved.invitedBy()),
                    "ORG_INVITATION_ACCEPTED",
                    saved.id(),
                    saved.organizationId(),
                    null,
                    "Organization invitation accepted",
                    "Your invitation to " + invitation.inviteeEmail() + " was accepted.");
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_INVITATION, saved.id(),
                WorkspaceActivityActions.ACCEPT_ORG_INVITATION,
                "Org invitation accepted by userId=" + userId);

        return OrgInvitationResponse.from(saved);
    }
}
