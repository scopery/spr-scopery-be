package com.company.scopery.modules.notification.advanced.channelpref.domain.model;
import java.time.Instant; import java.util.UUID;
public record NotificationChannelPreference(UUID id, UUID workspaceId, UUID userId, String categoryCode, String channelCode,
        boolean enabled, int version, Instant createdAt, Instant updatedAt) {
    public static NotificationChannelPreference create(UUID workspaceId, UUID userId, String category, String channel, boolean enabled) {
        Instant now = Instant.now();
        return new NotificationChannelPreference(UUID.randomUUID(), workspaceId, userId, category, channel, enabled, 0, now, now);
    }
    public NotificationChannelPreference withEnabled(boolean enabled) {
        return new NotificationChannelPreference(id, workspaceId, userId, categoryCode, channelCode, enabled, version, createdAt, Instant.now());
    }
}
