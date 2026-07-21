package com.company.scopery.modules.resourcecapacity.effortestimate.domain.model;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.enums.*;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record EffortEstimate(UUID id, UUID workspaceId, UUID projectId, String targetType, UUID targetId,
        UUID resourceRoleId, UUID resourceProfileId, EffortEstimateType estimateType, BigDecimal effortHours,
        BigDecimal confidencePercent, String reason, EffortEstimateStatus status,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static EffortEstimate create(UUID workspaceId, UUID projectId, String targetType, UUID targetId,
                                        EffortEstimateType type, BigDecimal hours, UUID roleId, UUID profileId, String reason) {
        if (hours == null || hours.signum() < 0) throw new IllegalArgumentException("effortHours must be non-negative");
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() → persist() (not merge/optimistic-lock).
        return new EffortEstimate(UUID.randomUUID(), workspaceId, projectId, targetType, targetId, roleId, profileId,
                type, hours, null, reason, EffortEstimateStatus.ACTIVE, null, null, 0, null, null);
    }
    public EffortEstimate supersede() {
        return new EffortEstimate(id, workspaceId, projectId, targetType, targetId, resourceRoleId, resourceProfileId,
                estimateType, effortHours, confidencePercent, reason, EffortEstimateStatus.SUPERSEDED,
                archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
