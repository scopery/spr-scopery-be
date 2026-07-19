package com.company.scopery.modules.notification.advanced.subscription.domain.model;
import java.time.Instant; import java.util.UUID;
public record NotificationSubscription(UUID id, UUID workspaceId, UUID userId, String targetType, UUID targetId,
        String subscriptionLevel, boolean autoSubscribed, String status, int version, Instant createdAt, Instant updatedAt) {
    public static NotificationSubscription create(UUID workspaceId, UUID userId, String targetType, UUID targetId, String level) {
        Instant now = Instant.now();
        return new NotificationSubscription(UUID.randomUUID(), workspaceId, userId, targetType, targetId, level, false, "ACTIVE", 0, now, now);
    }
    public NotificationSubscription unsubscribe() {
        return new NotificationSubscription(id, workspaceId, userId, targetType, targetId, subscriptionLevel, autoSubscribed, "INACTIVE", version, createdAt, Instant.now());
    }
}
