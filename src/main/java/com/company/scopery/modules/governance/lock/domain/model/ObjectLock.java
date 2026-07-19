package com.company.scopery.modules.governance.lock.domain.model;
import java.time.Instant; import java.util.UUID;
public record ObjectLock(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, UUID targetId, String lockType,
        String status, Instant lockedAt, UUID lockedBy, Instant releasedAt, UUID releasedBy, String reason,
        int version, Instant createdAt, Instant updatedAt) {
    public static ObjectLock create(UUID workspaceId, UUID projectId, String objectType, UUID targetId, String lockType, UUID actorId, String reason) {
        Instant now = Instant.now();
        return new ObjectLock(UUID.randomUUID(), workspaceId, projectId, objectType, targetId, lockType, "ACTIVE", now, actorId, null, null, reason, 0, now, now);
    }
    public ObjectLock release(UUID actorId) {
        Instant now = Instant.now();
        return new ObjectLock(id, workspaceId, projectId, objectTypeCode, targetId, lockType, "RELEASED", lockedAt, lockedBy, now, actorId, reason, version, createdAt, now);
    }
}
