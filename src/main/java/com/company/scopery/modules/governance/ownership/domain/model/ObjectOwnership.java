package com.company.scopery.modules.governance.ownership.domain.model;
import java.time.Instant; import java.util.UUID;
public record ObjectOwnership(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, UUID targetId,
        String ownerTargetType, UUID ownerTargetId, String status, Instant assignedAt, UUID assignedBy,
        Instant revokedAt, UUID revokedBy, int version, Instant createdAt, Instant updatedAt) {
    public static ObjectOwnership assign(UUID workspaceId, UUID projectId, String objectType, UUID targetId, String ownerType, UUID ownerId, UUID actorId) {
        Instant now = Instant.now();
        return new ObjectOwnership(UUID.randomUUID(), workspaceId, projectId, objectType, targetId, ownerType, ownerId, "ACTIVE", now, actorId, null, null, 0, now, now);
    }
    public ObjectOwnership revoke(UUID actorId) {
        Instant now = Instant.now();
        return new ObjectOwnership(id, workspaceId, projectId, objectTypeCode, targetId, ownerTargetType, ownerTargetId, "REVOKED", assignedAt, assignedBy, now, actorId, version, createdAt, now);
    }
}
