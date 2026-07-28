package com.company.scopery.modules.notification.notificationitem.domain.model;

import com.company.scopery.modules.notification.notificationitem.domain.enums.NotificationItemStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationItemRepository {

    NotificationItem save(NotificationItem item);

    Optional<NotificationItem> findById(UUID id);

    boolean existsByRecipientUserIdAndDedupKey(UUID recipientUserId, String dedupKey);

    Optional<NotificationItem> findByRecipientUserIdAndDedupKey(UUID recipientUserId, String dedupKey);

    List<NotificationItem> findBySourceResourceTypeAndSourceResourceId(String sourceResourceType, UUID sourceResourceId);

    List<NotificationItem> findBySourceResourceTypeInAndStatusIn(
            List<String> sourceResourceTypes, List<NotificationItemStatus> statuses);

    List<NotificationItem> findByRecipientUserId(UUID recipientUserId, boolean includeDismissed, int page, int size);

    long countByRecipientUserId(UUID recipientUserId, boolean includeDismissed);

    long countUnreadByRecipientUserId(UUID recipientUserId);

    int markAllRead(UUID recipientUserId);
}
