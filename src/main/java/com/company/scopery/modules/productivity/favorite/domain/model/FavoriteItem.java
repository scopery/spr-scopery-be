package com.company.scopery.modules.productivity.favorite.domain.model;
import java.time.Instant; import java.util.UUID;
public record FavoriteItem(UUID id, UUID workspaceId, UUID userId, String targetType, UUID targetId, String labelOverride,
        Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {
    public static FavoriteItem create(UUID workspaceId, UUID userId, String targetType, UUID targetId, String label) {
        Instant now = Instant.now();
        return new FavoriteItem(UUID.randomUUID(), workspaceId, userId, targetType, targetId, label, null, 0, now, now);
    }
    public FavoriteItem archive() { return new FavoriteItem(id, workspaceId, userId, targetType, targetId, labelOverride, Instant.now(), version, createdAt, Instant.now()); }
}
