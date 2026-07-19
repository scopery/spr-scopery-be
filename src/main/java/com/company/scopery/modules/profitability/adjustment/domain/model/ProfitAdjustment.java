package com.company.scopery.modules.profitability.adjustment.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ProfitAdjustment(UUID id, UUID workspaceId, UUID projectId, UUID profileId, String adjustmentType, BigDecimal amount,
        String reason, String status, String sourceLinkType, UUID sourceLinkId, int version, Instant createdAt, Instant updatedAt) {
    public static ProfitAdjustment create(UUID workspaceId, UUID projectId, UUID profileId, String type, BigDecimal amount, String reason) {
        Instant now = Instant.now();
        return new ProfitAdjustment(UUID.randomUUID(), workspaceId, projectId, profileId, type, amount, reason, "DRAFT", null, null, 0, now, now);
    }
    public ProfitAdjustment apply() {
        return new ProfitAdjustment(id, workspaceId, projectId, profileId, adjustmentType, amount, reason, "APPLIED", sourceLinkType, sourceLinkId, version, createdAt, Instant.now());
    }
}
