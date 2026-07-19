package com.company.scopery.modules.notification.notificationitem.application.response;

import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;

import java.time.Instant;
import java.util.UUID;

public record NotificationItemResponse(
        UUID id,
        UUID recipientUserId,
        UUID eventDefinitionId,
        String sourceSystem,
        String title,
        String bodyPreview,
        String severity,
        String priority,
        String actionType,
        String actionUrl,
        boolean mandatory,
        String status,
        Instant readAt,
        Instant dismissedAt,
        UUID workspaceId,
        Instant createdAt,
        Instant updatedAt
) {
    public static NotificationItemResponse from(NotificationItem item) {
        return new NotificationItemResponse(
                item.id(), item.recipientUserId(), item.eventDefinitionId(), item.sourceSystem(),
                item.title(), item.bodyPreview(), item.severity().name(), item.priority().name(),
                item.actionType(), item.actionUrl(), item.mandatory(), item.status().name(),
                item.readAt(), item.dismissedAt(), item.workspaceId(),
                item.createdAt(), item.updatedAt());
    }
}
