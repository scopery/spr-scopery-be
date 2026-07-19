package com.company.scopery.modules.quality.releaseitem.domain.model;
import com.company.scopery.modules.quality.releaseitem.domain.enums.ReleaseItemStatus;
import java.time.Instant; import java.util.UUID;
public record ReleaseItem(UUID id, UUID projectId, UUID releasePackageId, String targetType, UUID targetId, boolean required,
                       ReleaseItemStatus status, String notes, Instant archivedAt, UUID archivedBy, int version, Instant createdAt) {
    public static ReleaseItem create(UUID projectId, UUID releasePackageId, String targetType, UUID targetId, boolean required, ReleaseItemStatus status, String notes) {
        return new ReleaseItem(UUID.randomUUID(), projectId, releasePackageId, targetType, targetId, required, status, notes, null, null, 0, Instant.now());
    }
    public ReleaseItem archive(UUID actorId) {
        return new ReleaseItem(id, projectId, releasePackageId, targetType, targetId, required, status, notes, Instant.now(), actorId, version, createdAt);
    }
}
