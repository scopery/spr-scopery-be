package com.company.scopery.modules.profitability.revenuesource.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitRevenueSource(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profileId,
        String sourceType,
        UUID sourceId,
        BigDecimal amount,
        String currency,
        boolean includedInForecast,
        String confidence,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitRevenueSource create(
            UUID workspaceId,
            UUID projectId,
            UUID profileId,
            String sourceType,
            UUID sourceId,
            BigDecimal amount,
            String currency,
            boolean includedInForecast,
            String confidence) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Revenue amount must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        Instant now = Instant.now();
        return new ProfitRevenueSource(
                UUID.randomUUID(),
                workspaceId,
                projectId,
                profileId,
                sourceType,
                sourceId,
                amount,
                currency.trim(),
                includedInForecast,
                confidence,
                "ACTIVE",
                0,
                now,
                now);
    }

    public ProfitRevenueSource update(
            String sourceType,
            UUID sourceId,
            BigDecimal amount,
            String currency,
            boolean includedInForecast,
            String confidence) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Revenue amount must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        return new ProfitRevenueSource(
                id, workspaceId, projectId, profileId, sourceType, sourceId, amount, currency.trim(),
                includedInForecast, confidence, status, version, createdAt, Instant.now());
    }

    public ProfitRevenueSource archive() {
        return new ProfitRevenueSource(
                id, workspaceId, projectId, profileId, sourceType, sourceId, amount, currency,
                includedInForecast, confidence, "ARCHIVED", version, createdAt, Instant.now());
    }
}
