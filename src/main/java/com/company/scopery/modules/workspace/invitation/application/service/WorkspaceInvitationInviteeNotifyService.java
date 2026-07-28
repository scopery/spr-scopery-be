package com.company.scopery.modules.workspace.invitation.application.service;

import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Invitee-facing side effects in a separate transaction so failures never roll back
 * the invitation itself. V1: Work Inbox only (no Accept CTA on Notification).
 */
@Service
public class WorkspaceInvitationInviteeNotifyService {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceInvitationInviteeNotifyService.class);

    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public WorkspaceInvitationInviteeNotifyService(InAppDeliveryService inAppDeliveryService,
                                                    WorkspaceAudienceResolver audienceResolver) {
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyInvitee(IamUser invitee, Workspace ws, WorkspaceInvitation invitation, String acceptUrl) {
        try {
            inAppDeliveryService.deliverWorkInbox(
                    ws.id(),
                    audienceResolver.explicitUser(invitee.id()),
                    InAppDeliveryService.SOURCE_WORKSPACE_INVITATION,
                    invitation.id(),
                    InAppDeliveryService.ACTION_ACCEPT_WORKSPACE,
                    "Invite to join " + ws.name(),
                    "HIGH",
                    invitation.expiresAt());
            log.debug("Work inbox invite item created for user {} invitation {}", invitee.id(), invitation.id());
        } catch (Exception ex) {
            log.warn("Workspace invitation {}: work-inbox failed for {}: {}",
                    invitation.id(), invitee.id(), ex.toString());
        }
    }
}
