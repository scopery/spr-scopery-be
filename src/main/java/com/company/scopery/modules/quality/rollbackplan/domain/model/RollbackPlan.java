package com.company.scopery.modules.quality.rollbackplan.domain.model;
import com.company.scopery.modules.quality.rollbackplan.domain.enums.RollbackPlanStatus;
import java.time.Instant; import java.util.UUID;
public record RollbackPlan(UUID id, UUID projectId, UUID releasePackageId, UUID deploymentRecordId, String title, String description,
                       UUID ownerUserId, RollbackPlanStatus status, String stepsJson, Instant approvedAt, UUID approvedBy,
                       Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static RollbackPlan create(UUID projectId, UUID releasePackageId, UUID deploymentRecordId, String title, String description, UUID ownerUserId, String stepsJson) {
        Instant now = Instant.now();
        return new RollbackPlan(UUID.randomUUID(), projectId, releasePackageId, deploymentRecordId, title, description, ownerUserId,
                RollbackPlanStatus.DRAFT, stepsJson, null, null, null, null, 0, now, now);
    }
    public RollbackPlan approve(UUID actorId) {
        return new RollbackPlan(id, projectId, releasePackageId, deploymentRecordId, title, description, ownerUserId,
                RollbackPlanStatus.APPROVED, stepsJson, Instant.now(), actorId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public RollbackPlan markReady() {
        return new RollbackPlan(id, projectId, releasePackageId, deploymentRecordId, title, description, ownerUserId,
                RollbackPlanStatus.READY, stepsJson, approvedAt, approvedBy, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public RollbackPlan archive(UUID actorId) {
        return new RollbackPlan(id, projectId, releasePackageId, deploymentRecordId, title, description, ownerUserId,
                RollbackPlanStatus.ARCHIVED, stepsJson, approvedAt, approvedBy, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
