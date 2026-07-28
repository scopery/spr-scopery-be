package com.company.scopery.modules.workspace.shared.service;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import com.company.scopery.modules.workspace.invitation.domain.enums.WorkspaceInvitationStatus;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Dismiss invitation-related NotificationItem + WorkInboxItem after accept / cancel / revoke.
 */
@Service
public class InvitationInboxCleanupService {

    private static final Logger log = LoggerFactory.getLogger(InvitationInboxCleanupService.class);

    public static final String ORG_INVITATION = "ORG_INVITATION";
    public static final String WORKSPACE_INVITATION = "WORKSPACE_INVITATION";

    private final NotificationItemRepository notificationItemRepository;
    private final WorkInboxItemRepository workInboxItemRepository;
    private final OrgInvitationRepository orgInvitationRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;

    public InvitationInboxCleanupService(NotificationItemRepository notificationItemRepository,
                                          WorkInboxItemRepository workInboxItemRepository,
                                          OrgInvitationRepository orgInvitationRepository,
                                          WorkspaceInvitationRepository workspaceInvitationRepository) {
        this.notificationItemRepository = notificationItemRepository;
        this.workInboxItemRepository = workInboxItemRepository;
        this.orgInvitationRepository = orgInvitationRepository;
        this.workspaceInvitationRepository = workspaceInvitationRepository;
    }

    @Transactional
    public void dismissOrgInvitation(UUID invitationId, UUID recipientUserId) {
        dismiss(ORG_INVITATION, invitationId, recipientUserId);
    }

    @Transactional
    public void dismissWorkspaceInvitation(UUID invitationId, UUID recipientUserId) {
        dismiss(WORKSPACE_INVITATION, invitationId, recipientUserId);
    }

    /** Cancel/revoke — dismiss for all recipients tied to this invitation. */
    @Transactional
    public void dismissOrgInvitationForAll(UUID invitationId) {
        dismiss(ORG_INVITATION, invitationId, null);
    }

    @Transactional
    public void dismissWorkspaceInvitationForAll(UUID invitationId) {
        dismiss(WORKSPACE_INVITATION, invitationId, null);
    }

    /**
     * One-shot / startup backfill: dismiss open invitation notifications & work-inbox rows
     * whose invitation is no longer PENDING (accepted / cancelled / revoked / expired / missing).
     *
     * @return int[2] = {notificationsDismissed, workInboxDismissed}
     */
    @Transactional
    public int[] backfillStaleInvitationInbox() {
        int notifications = 0;
        int workInbox = 0;

        List<NotificationItem> openNotifs = notificationItemRepository.findBySourceResourceTypeInAndStatusIn(
                List.of(ORG_INVITATION, WORKSPACE_INVITATION),
                List.of(NotificationItemStatus.UNREAD, NotificationItemStatus.READ));
        for (NotificationItem item : openNotifs) {
            if (item.sourceResourceId() == null) continue;
            if (!isInvitationTerminal(item.sourceResourceType(), item.sourceResourceId())) continue;
            item.dismiss();
            notificationItemRepository.save(item);
            notifications++;
        }

        List<WorkInboxItem> openInbox = workInboxItemRepository.findBySourceTypeInAndStatusIn(
                List.of(ORG_INVITATION, WORKSPACE_INVITATION),
                List.of("ACTIVE", "READ", "SNOOZED"));
        for (WorkInboxItem item : openInbox) {
            if (item.sourceId() == null) continue;
            if (!isInvitationTerminal(item.sourceType(), item.sourceId())) continue;
            workInboxItemRepository.save(item.dismiss());
            workInbox++;
        }

        return new int[]{notifications, workInbox};
    }

    private boolean isInvitationTerminal(String sourceType, UUID invitationId) {
        if (ORG_INVITATION.equals(sourceType)) {
            return orgInvitationRepository.findById(invitationId)
                    .map(inv -> inv.status() != OrgInvitationStatus.PENDING)
                    .orElse(true); // orphan → dismiss
        }
        if (WORKSPACE_INVITATION.equals(sourceType)) {
            return workspaceInvitationRepository.findById(invitationId)
                    .map(inv -> inv.status() != WorkspaceInvitationStatus.PENDING)
                    .orElse(true);
        }
        return false;
    }

    @Transactional
    public void dismissBySourceForAll(String sourceType, UUID sourceId) {
        dismiss(sourceType, sourceId, null);
    }

    private void dismiss(String sourceType, UUID invitationId, UUID recipientUserId) {
        try {
            dismissNotifications(sourceType, invitationId, recipientUserId);
            dismissWorkInbox(sourceType, invitationId, recipientUserId);
        } catch (Exception ex) {
            // Never fail accept/cancel because inbox cleanup failed
            log.warn("Failed to dismiss inbox items for {} {}: {}", sourceType, invitationId, ex.toString());
        }
    }

    private void dismissNotifications(String sourceType, UUID invitationId, UUID recipientUserId) {
        Set<UUID> dismissed = new LinkedHashSet<>();

        if (recipientUserId != null) {
            String dedup = sourceType + ":" + invitationId + ":" + recipientUserId;
            notificationItemRepository.findByRecipientUserIdAndDedupKey(recipientUserId, dedup)
                    .ifPresent(item -> {
                        if (item.status() != NotificationItemStatus.DISMISSED) {
                            item.dismiss();
                            notificationItemRepository.save(item);
                        }
                        dismissed.add(item.id());
                    });
        }

        List<NotificationItem> bySource =
                notificationItemRepository.findBySourceResourceTypeAndSourceResourceId(sourceType, invitationId);
        for (NotificationItem item : bySource) {
            if (dismissed.contains(item.id())) continue;
            if (recipientUserId != null && !recipientUserId.equals(item.recipientUserId())) continue;
            if (item.status() == NotificationItemStatus.DISMISSED) continue;
            item.dismiss();
            notificationItemRepository.save(item);
        }
    }

    private void dismissWorkInbox(String sourceType, UUID invitationId, UUID recipientUserId) {
        List<WorkInboxItem> items = recipientUserId != null
                ? workInboxItemRepository.findByUserIdAndSourceTypeAndSourceId(recipientUserId, sourceType, invitationId)
                : workInboxItemRepository.findBySourceTypeAndSourceId(sourceType, invitationId);
        for (WorkInboxItem item : items) {
            if ("DISMISSED".equals(item.status())) continue;
            workInboxItemRepository.save(item.dismiss());
        }
    }
}
