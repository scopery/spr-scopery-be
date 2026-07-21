package com.company.scopery.modules.resourcecapacity.threshold.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record UtilizationThresholdPolicy(UUID id, UUID workspaceId, UUID projectId,
        BigDecimal underAllocatedPercent, BigDecimal healthyMinPercent, BigDecimal healthyMaxPercent,
        BigDecimal watchMaxPercent, BigDecimal overloadedPercent, BigDecimal criticalOverloadPercent,
        boolean enabled, int version, Instant createdAt, Instant updatedAt) {
    public static UtilizationThresholdPolicy defaults(UUID workspaceId, UUID projectId) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() → persist() (not merge/optimistic-lock).
        return new UtilizationThresholdPolicy(UUID.randomUUID(), workspaceId, projectId,
                new BigDecimal("50"), new BigDecimal("50"), new BigDecimal("85"),
                new BigDecimal("100"), new BigDecimal("100"), new BigDecimal("120"),
                true, 0, null, null);
    }
}
