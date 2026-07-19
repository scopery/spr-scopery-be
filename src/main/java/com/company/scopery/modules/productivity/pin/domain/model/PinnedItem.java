package com.company.scopery.modules.productivity.pin.domain.model;
import java.time.Instant; import java.util.UUID;
public record PinnedItem(UUID id, UUID workspaceId, UUID projectId, String scope, UUID ownerUserId, String targetType, UUID targetId,
        int sortOrder, Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {
    public static PinnedItem create(UUID workspaceId, UUID projectId, String scope, UUID ownerUserId, String targetType, UUID targetId, int sortOrder) {
        Instant now = Instant.now();
        return new PinnedItem(UUID.randomUUID(), workspaceId, projectId, scope, ownerUserId, targetType, targetId, sortOrder, null, 0, now, now);
    }
    public PinnedItem archive() {
        return new PinnedItem(id, workspaceId, projectId, scope, ownerUserId, targetType, targetId, sortOrder, Instant.now(), version, createdAt, Instant.now());
    }
}
