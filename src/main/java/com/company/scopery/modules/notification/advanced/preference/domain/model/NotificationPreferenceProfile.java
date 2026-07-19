package com.company.scopery.modules.notification.advanced.preference.domain.model;
import java.time.Instant; import java.util.UUID;
public record NotificationPreferenceProfile(UUID id, UUID workspaceId, UUID userId, String timezone, String defaultMode,
        boolean digestEnabled, boolean quietHoursEnabled, String quietHoursStart, String quietHoursEnd,
        int version, Instant createdAt, Instant updatedAt) {
    public static NotificationPreferenceProfile create(UUID workspaceId, UUID userId) {
        Instant now = Instant.now();
        return new NotificationPreferenceProfile(UUID.randomUUID(), workspaceId, userId, "UTC", "INSTANT", false, false, null, null, 0, now, now);
    }
    public NotificationPreferenceProfile update(String timezone, String defaultMode, boolean digestEnabled, boolean quietHoursEnabled, String start, String end) {
        return new NotificationPreferenceProfile(id, workspaceId, userId, timezone, defaultMode, digestEnabled, quietHoursEnabled, start, end, version, createdAt, Instant.now());
    }
}
