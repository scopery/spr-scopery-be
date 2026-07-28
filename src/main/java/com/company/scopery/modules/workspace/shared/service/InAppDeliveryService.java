package com.company.scopery.modules.workspace.shared.service;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationPriority;
import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationSeverity;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItemRepository;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

/**
 * Fan-out in-app delivery: Work Inbox (actionable) vs Notification (FYI, no action CTA).
 */
@Service
public class InAppDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(InAppDeliveryService.class);

    public static final String SOURCE_ORG_INVITATION = "ORG_INVITATION";
    public static final String SOURCE_WORKSPACE_INVITATION = "WORKSPACE_INVITATION";
    public static final String SOURCE_JOIN_REQUEST = "JOIN_REQUEST";
    public static final String ACTION_ACCEPT_ORG = "ACCEPT_ORG_INVITATION";
    public static final String ACTION_ACCEPT_WORKSPACE = "ACCEPT_WORKSPACE_INVITATION";
    public static final String ACTION_REVIEW_JOIN = "REVIEW_JOIN_REQUEST";

    private final NotificationItemRepository notificationItemRepository;
    private final WorkInboxItemRepository workInboxItemRepository;
    private final WorkspaceUserContextRepository workspaceUserContextRepository;

    public InAppDeliveryService(NotificationItemRepository notificationItemRepository,
                                 WorkInboxItemRepository workInboxItemRepository,
                                 WorkspaceUserContextRepository workspaceUserContextRepository) {
        this.notificationItemRepository = notificationItemRepository;
        this.workInboxItemRepository = workInboxItemRepository;
        this.workspaceUserContextRepository = workspaceUserContextRepository;
    }

    @Transactional
    public void deliverWorkInbox(UUID preferredWorkspaceId,
                                  Collection<UUID> userIds,
                                  String sourceType,
                                  UUID sourceId,
                                  String actionType,
                                  String title,
                                  String priority,
                                  Instant dueAt) {
        if (userIds == null || userIds.isEmpty()) return;
        for (UUID userId : userIds) {
            if (userId == null) continue;
            try {
                UUID workspaceId = resolveInboxWorkspaceId(userId, preferredWorkspaceId);
                if (workspaceId == null) {
                    log.debug("Skip work-inbox {} for user {}: no workspace context", sourceType, userId);
                    continue;
                }
                // Avoid duplicates for same source+user (any workspace)
                boolean exists = workInboxItemRepository
                        .findByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId)
                        .stream()
                        .anyMatch(i -> !"DISMISSED".equals(i.status()));
                if (exists) continue;

                workInboxItemRepository.save(WorkInboxItem.create(
                        workspaceId, userId, sourceType, sourceId, actionType, title,
                        priority != null ? priority : "NORMAL", dueAt));
            } catch (Exception ex) {
                log.warn("Work-inbox deliver failed source={} user={}: {}", sourceType, userId, ex.toString());
            }
        }
    }

    /**
     * FYI notification — never sets actionType/actionUrl (no Accept/Open CTA).
     */
    @Transactional
    public void deliverNotificationFyi(Collection<UUID> userIds,
                                        String sourceResourceType,
                                        UUID sourceResourceId,
                                        UUID organizationId,
                                        UUID workspaceId,
                                        String title,
                                        String bodyPreview) {
        if (userIds == null || userIds.isEmpty()) return;
        for (UUID userId : userIds) {
            if (userId == null) continue;
            try {
                String dedup = sourceResourceType + ":" + sourceResourceId + ":" + userId;
                if (notificationItemRepository.existsByRecipientUserIdAndDedupKey(userId, dedup)) {
                    continue;
                }
                NotificationItem item = NotificationItem.create(
                        userId,
                        null,
                        "SCOPERY_WORKSPACE",
                        sourceResourceType,
                        sourceResourceId,
                        organizationId,
                        workspaceId,
                        null,
                        title,
                        bodyPreview,
                        NotificationSeverity.INFO,
                        NotificationPriority.NORMAL,
                        null,
                        null,
                        dedup,
                        false,
                        MDC.get("traceId"));
                notificationItemRepository.save(item);
            } catch (Exception ex) {
                log.warn("FYI notification deliver failed source={} user={}: {}",
                        sourceResourceType, userId, ex.toString());
            }
        }
    }

    private UUID resolveInboxWorkspaceId(UUID userId, UUID preferredWorkspaceId) {
        if (preferredWorkspaceId != null) return preferredWorkspaceId;
        return workspaceUserContextRepository.findByUserId(userId)
                .map(WorkspaceUserContext::currentWorkspaceId)
                .filter(id -> id != null)
                .orElse(null);
    }
}
