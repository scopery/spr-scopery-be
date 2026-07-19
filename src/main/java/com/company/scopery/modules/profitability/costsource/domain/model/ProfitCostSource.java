package com.company.scopery.modules.profitability.costsource.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitCostSource(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profileId,
        String sourceType,
        UUID sourceId,
        BigDecimal effortHours,
        BigDecimal rateAmount,
        BigDecimal amount,
        String currency,
        boolean includedInForecast,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitCostSource create(
            UUID workspaceId,
            UUID projectId,
            UUID profileId,
            String sourceType,
            UUID sourceId,
            BigDecimal effortHours,
            BigDecimal rateAmount,
            BigDecimal amount,
            String currency,
            boolean includedInForecast) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Cost amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        Instant now = Instant.now();
        return new ProfitCostSource(
                UUID.randomUUID(),
                workspaceId,
                projectId,
                profileId,
                sourceType,
                sourceId,
                effortHours,
                rateAmount,
                amount,
                currency.trim(),
                includedInForecast,
                "ACTIVE",
                0,
                now,
                now);
    }

    public ProfitCostSource update(
            String sourceType,
            UUID sourceId,
            BigDecimal effortHours,
            BigDecimal rateAmount,
            BigDecimal amount,
            String currency,
            boolean includedInForecast) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Cost amount must be non-negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        return new ProfitCostSource(
                id, workspaceId, projectId, profileId, sourceType, sourceId, effortHours, rateAmount, amount,
                currency.trim(), includedInForecast, status, version, createdAt, Instant.now());
    }

    public ProfitCostSource archive() {
        return new ProfitCostSource(
                id, workspaceId, projectId, profileId, sourceType, sourceId, effortHours, rateAmount, amount,
                currency, includedInForecast, "ARCHIVED", version, createdAt, Instant.now());
    }
}
