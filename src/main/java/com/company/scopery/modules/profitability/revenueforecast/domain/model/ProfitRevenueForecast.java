package com.company.scopery.modules.profitability.revenueforecast.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProfitRevenueForecast(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID profitabilityProfileId,
        String forecastType,
        String currency,
        BigDecimal forecastAmount,
        BigDecimal confidencePercent,
        LocalDate forecastDate,
        String assumptionNotes,
        String generatedBy,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitRevenueForecast create(
            UUID workspaceId,
            UUID projectId,
            UUID profileId,
            String forecastType,
            String currency,
            BigDecimal forecastAmount,
            BigDecimal confidencePercent,
            LocalDate forecastDate,
            String assumptionNotes) {
        if (forecastAmount == null) {
            throw new IllegalArgumentException("Forecast amount required");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency required");
        }
        Instant now = Instant.now();
        return new ProfitRevenueForecast(
                UUID.randomUUID(), workspaceId, projectId, profileId,
                forecastType, currency.trim(), forecastAmount, confidencePercent, forecastDate,
                assumptionNotes, "MANUAL", "ACTIVE", 0, now, now);
    }

    public ProfitRevenueForecast supersede() {
        return new ProfitRevenueForecast(
                id, workspaceId, projectId, profitabilityProfileId,
                forecastType, currency, forecastAmount, confidencePercent, forecastDate,
                assumptionNotes, generatedBy, "SUPERSEDED", version, createdAt, Instant.now());
    }

    public ProfitRevenueForecast archive() {
        return new ProfitRevenueForecast(
                id, workspaceId, projectId, profitabilityProfileId,
                forecastType, currency, forecastAmount, confidencePercent, forecastDate,
                assumptionNotes, generatedBy, "ARCHIVED", version, createdAt, Instant.now());
    }
}
