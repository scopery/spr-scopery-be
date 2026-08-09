package com.company.scopery.modules.profitability.thresholdpolicy.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitThresholdPolicy(
        UUID id,
        UUID projectId,
        BigDecimal healthyMarginPercent,
        BigDecimal watchMarginPercent,
        BigDecimal atRiskMarginPercent,
        BigDecimal lossRiskMarginPercent,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitThresholdPolicy createDefault(UUID projectId) {
        return new ProfitThresholdPolicy(
                UUID.randomUUID(),
                projectId,
                new BigDecimal("20.0"),
                new BigDecimal("10.0"),
                new BigDecimal("5.0"),
                new BigDecimal("0.0"),
                null,
                null,
                null
        );
    }
}
