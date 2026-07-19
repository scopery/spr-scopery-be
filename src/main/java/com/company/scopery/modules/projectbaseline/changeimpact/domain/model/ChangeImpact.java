package com.company.scopery.modules.projectbaseline.changeimpact.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ChangeImpact(
        UUID id,
        UUID changeRequestId,
        UUID projectId,
        String currencyCode,
        String scopeImpact,
        Integer scheduleImpactDays,
        BigDecimal estimateHoursImpact,
        BigDecimal laborCostImpact,
        BigDecimal directCostImpact,
        BigDecimal overheadImpact,
        BigDecimal revenueImpact,
        BigDecimal grossMarginImpact,
        BigDecimal pbtImpact,
        BigDecimal quoteAmountImpact,
        String riskImpact,
        String impactSummaryJson,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChangeImpact create(UUID changeRequestId, UUID projectId, String currencyCode) {
        return new ChangeImpact(UUID.randomUUID(), changeRequestId, projectId, currencyCode,
                null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null);
    }

    public ChangeImpact update(
            String currencyCode, String scopeImpact, Integer scheduleImpactDays,
            BigDecimal estimateHoursImpact, BigDecimal laborCostImpact, BigDecimal directCostImpact,
            BigDecimal overheadImpact, BigDecimal revenueImpact, BigDecimal grossMarginImpact,
            BigDecimal pbtImpact, BigDecimal quoteAmountImpact, String riskImpact, String impactSummaryJson) {
        return new ChangeImpact(id, changeRequestId, projectId, currencyCode, scopeImpact, scheduleImpactDays,
                estimateHoursImpact, laborCostImpact, directCostImpact, overheadImpact, revenueImpact,
                grossMarginImpact, pbtImpact, quoteAmountImpact, riskImpact, impactSummaryJson,
                version, createdAt, updatedAt);
    }

    public boolean hasAnyImpact() {
        return scopeImpact != null || scheduleImpactDays != null || estimateHoursImpact != null
                || laborCostImpact != null || directCostImpact != null || overheadImpact != null
                || revenueImpact != null || grossMarginImpact != null || pbtImpact != null
                || quoteAmountImpact != null || riskImpact != null;
    }
}
