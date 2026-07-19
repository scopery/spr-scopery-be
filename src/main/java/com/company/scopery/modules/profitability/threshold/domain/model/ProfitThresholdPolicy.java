package com.company.scopery.modules.profitability.threshold.domain.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record ProfitThresholdPolicy(UUID id, UUID workspaceId, UUID projectId, BigDecimal healthyMarginPercent,
        BigDecimal watchMarginPercent, BigDecimal atRiskMarginPercent, BigDecimal lossRiskMarginPercent,
        int version, Instant createdAt, Instant updatedAt) {
    public static ProfitThresholdPolicy defaults(UUID workspaceId, UUID projectId) {
        Instant now = Instant.now();
        return new ProfitThresholdPolicy(UUID.randomUUID(), workspaceId, projectId,
                BigDecimal.valueOf(30), BigDecimal.valueOf(20), BigDecimal.valueOf(10), BigDecimal.ZERO, 0, now, now);
    }
    public ProfitThresholdPolicy update(BigDecimal healthy, BigDecimal watch, BigDecimal atRisk, BigDecimal lossRisk) {
        return new ProfitThresholdPolicy(id, workspaceId, projectId,
                healthy != null ? healthy : healthyMarginPercent,
                watch != null ? watch : watchMarginPercent,
                atRisk != null ? atRisk : atRiskMarginPercent,
                lossRisk != null ? lossRisk : lossRiskMarginPercent,
                version, createdAt, Instant.now());
    }
}
