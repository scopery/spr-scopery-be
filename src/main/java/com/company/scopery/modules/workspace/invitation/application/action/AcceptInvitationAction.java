package com.company.scopery.modules.workspace.invitation.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.invitation.application.command.AcceptInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.member.application.service.WorkspaceMembershipEnrollmentService;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMembershipEnrollmentService;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class AcceptInvitationAction {

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceMembershipEnrollmentService workspaceEnrollmentService;
    private final OrgMembershipEnrollmentService orgEnrollmentService;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;
    private final InvitationInboxCleanupService inboxCleanupService;
    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public AcceptInvitationAction(WorkspaceInvitationRepository invitationRepository,
                                   WorkspaceMemberRepository memberRepository,
                                   WorkspaceMembershipEnrollmentService workspaceEnrollmentService,
                                   OrgMembershipEnrollmentService orgEnrollmentService,
                                   WorkspaceRepository workspaceRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   EmailNotificationTriggerPublisher notificationPublisher,
                                   WorkspaceActivityLogger activityLogger,
                                   InvitationInboxCleanupService inboxCleanupService,
                                   InAppDeliveryService inAppDeliveryService,
                                   WorkspaceAudienceResolver audienceResolver) {
        this.invitationRepository = invitationRepository;
        this.memberRepository = memberRepository;
        this.workspaceEnrollmentService = workspaceEnrollmentService;
        this.orgEnrollmentService = orgEnrollmentService;
        this.workspaceRepository = workspaceRepository;
        this.currentUserService = currentUserService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
        this.inboxCleanupService = inboxCleanupService;
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional
    public WorkspaceInvitationResponse execute(AcceptInvitationCommand command) {
        String codeHash = InvitationCodeHasher.hash(command.rawCode());
        WorkspaceInvitation invitation = invitationRepository.findByCodeHash(codeHash)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found", null));
        return accept(invitation);
    }

    /**
     * Accept by invitation id (Work Inbox / in-app notification CTA).
     * When the invite targeted an email, current user email must match.
     */
    @Transactional
    public WorkspaceInvitationResponse executeById(UUID invitationId) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                        "Invitation not found", null));
        var user = currentUserService.resolveCurrentUser();
        if (invitation.invitedEmail() != null && !invitation.invitedEmail().isBlank()
                && !invitation.invitedEmail().equalsIgnoreCase(user.email().value())) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_NOT_FOUND,
                    "This invitation was sent to a different email", null);
        }
        return accept(invitation);
    }

    private WorkspaceInvitationResponse accept(WorkspaceInvitation invitation) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();

        if (memberRepository.isActiveMember(invitation.workspaceId(), currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_ALREADY_MEMBER,
                    "You are already a member of this workspace", null);
        }

        Workspace ws = workspaceRepository.findById(invitation.workspaceId()).orElse(null);
        if (ws != null) {
            // Create or reinstate org membership so kick-from-org users can rejoin via workspace invite.
            orgEnrollmentService.ensureActiveMembershipQuiet(
                    ws.organizationId(),
                    currentUserId,
                    OrgMembershipType.MEMBER,
                    OrgMembershipSource.WORKSPACE_INVITATION);
        }

        WorkspaceInvitation updated = invitation.accept();
        invitationRepository.save(updated);

        workspaceEnrollmentService.ensureActiveMembership(invitation.workspaceId(), currentUserId, false);

        inboxCleanupService.dismissWorkspaceInvitation(updated.id(), currentUserId);

        if (updated.createdByUserId() != null) {
            inAppDeliveryService.deliverNotificationFyi(
                    audienceResolver.explicitUser(updated.createdByUserId()),
                    "WORKSPACE_INVITATION_ACCEPTED",
                    updated.id(),
                    ws != null ? ws.organizationId() : null,
                    invitation.workspaceId(),
                    "Workspace invitation accepted",
                    "Someone accepted your workspace invitation.");
        }

        notificationPublisher.publish(new EmailNotificationTriggerPayload(
                null, "WORKSPACE", "WORKSPACE_INVITATION_ACCEPTED",
                invitation.workspaceId(), currentUserId, Map.of()));

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_INVITATION, updated.id(),
                WorkspaceActivityActions.ACCEPT_INVITATION, "Invitation accepted");

        return WorkspaceInvitationResponse.from(updated);
    }
}
