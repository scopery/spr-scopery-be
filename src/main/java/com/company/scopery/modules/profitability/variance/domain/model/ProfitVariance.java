package com.company.scopery.modules.profitability.variance.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitVariance(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profitabilityProfileId,
        String varianceType,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal varianceAmount,
        BigDecimal variancePercent,
        String currency,
        String explanation,
        UUID sourceSnapshotId,
        int version,
        Instant createdAt
) {
    public static ProfitVariance create(
            UUID workspaceId,
            UUID projectId,
            UUID profileId,
            String varianceType,
            BigDecimal fromAmount,
            BigDecimal toAmount,
            BigDecimal varianceAmount,
            BigDecimal variancePercent,
            String currency,
            String explanation,
            UUID sourceSnapshotId) {
        if (varianceType == null || varianceType.isBlank()) {
            throw new IllegalArgumentException("Variance type required");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency required");
        }
        return new ProfitVariance(
                UUID.randomUUID(), workspaceId, projectId, profileId,
                varianceType, fromAmount, toAmount, varianceAmount, variancePercent,
                currency, explanation, sourceSnapshotId, 0, Instant.now());
    }
}
