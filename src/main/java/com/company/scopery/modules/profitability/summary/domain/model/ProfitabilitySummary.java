package com.company.scopery.modules.profitability.summary.domain.model;

import com.company.scopery.modules.profitability.summary.domain.enums.ProfitHealthStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitabilitySummary(
        UUID id,
        UUID projectId,
        String currency,
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        ProfitHealthStatus healthStatus,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitabilitySummary create(UUID projectId, String currency) {
        return new ProfitabilitySummary(
                UUID.randomUUID(),
                projectId,
                currency,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                ProfitHealthStatus.HEALTHY,
                null,
                null,
                null
        );
    }
}
