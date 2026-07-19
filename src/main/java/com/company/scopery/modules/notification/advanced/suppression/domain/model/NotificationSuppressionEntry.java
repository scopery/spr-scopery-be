package com.company.scopery.modules.notification.advanced.suppression.domain.model;
import java.time.Instant; import java.util.UUID;
public record NotificationSuppressionEntry(UUID id, UUID workspaceId, UUID projectId, UUID userId, String categoryCode,
        String channelCode, String reasonCode, String sourceType, UUID sourceId, Instant suppressedAt, Instant expiresAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static NotificationSuppressionEntry create(UUID workspaceId, UUID projectId, UUID userId, String category,
                                                      String channel, String reason, String sourceType, UUID sourceId) {
        Instant now = Instant.now();
        return new NotificationSuppressionEntry(UUID.randomUUID(), workspaceId, projectId, userId, category, channel,
                reason, sourceType, sourceId, now, null, 0, now, now);
    }
}
