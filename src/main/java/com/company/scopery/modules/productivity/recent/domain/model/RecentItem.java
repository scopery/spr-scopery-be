package com.company.scopery.modules.productivity.recent.domain.model;
import java.time.Instant; import java.util.UUID;
public record RecentItem(UUID id, UUID workspaceId, String principalType, UUID userId, UUID externalPortalAccountId,
        String targetType, UUID targetId, String titleSnapshot, Instant viewedAt, int version, Instant createdAt, Instant updatedAt) {
    public static RecentItem record(UUID workspaceId, UUID userId, String targetType, UUID targetId, String title) {
        Instant now = Instant.now();
        return new RecentItem(UUID.randomUUID(), workspaceId, "USER", userId, null, targetType, targetId, title, now, 0, now, now);
    }
}
