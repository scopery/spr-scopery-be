package com.company.scopery.modules.profitability.ratecard.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitRateCard(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String rateCode,
        String name,
        String rateType,
        String roleName,
        UUID teamId,
        String currency,
        BigDecimal amountPerHour,
        BigDecimal amountPerDay,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitRateCard create(
            UUID workspaceId,
            UUID projectId,
            String rateCode,
            String name,
            String rateType,
            String roleName,
            UUID teamId,
            String currency,
            BigDecimal amountPerHour,
            BigDecimal amountPerDay) {
        if (rateCode == null || rateCode.isBlank()) {
            throw new IllegalArgumentException("Rate code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        Instant now = Instant.now();
        return new ProfitRateCard(
                UUID.randomUUID(),
                workspaceId,
                projectId,
                rateCode.trim(),
                name.trim(),
                rateType,
                roleName,
                teamId,
                currency.trim(),
                amountPerHour,
                amountPerDay,
                "ACTIVE",
                0,
                now,
                now);
    }

    public ProfitRateCard update(
            String name,
            String roleName,
            String currency,
            BigDecimal amountPerHour,
            BigDecimal amountPerDay) {
        return new ProfitRateCard(
                id, workspaceId, projectId, rateCode, name, rateType, roleName, teamId,
                currency, amountPerHour, amountPerDay, status, version, createdAt, Instant.now());
    }

    public ProfitRateCard archive() {
        if ("ARCHIVED".equals(status)) {
            throw new IllegalStateException("Rate card is already archived");
        }
        return new ProfitRateCard(
                id, workspaceId, projectId, rateCode, name, rateType, roleName, teamId,
                currency, amountPerHour, amountPerDay, "ARCHIVED", version, createdAt, Instant.now());
    }
}
