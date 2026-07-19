package com.company.scopery.modules.projectbaseline.changeimpact.application.response;

import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpact;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;

public record ChangeImpactResponse(
        UUID id, UUID changeRequestId, UUID projectId, String currencyCode, String scopeImpact,
        Integer scheduleImpactDays, BigDecimal estimateHoursImpact, BigDecimal laborCostImpact,
        BigDecimal directCostImpact, BigDecimal overheadImpact, BigDecimal revenueImpact,
        BigDecimal grossMarginImpact, BigDecimal pbtImpact, BigDecimal quoteAmountImpact,
        String riskImpact, String impactSummaryJson, Instant createdAt, Instant updatedAt
) {
    public static ChangeImpactResponse from(ChangeImpact i, boolean includeFinance) {
        return new ChangeImpactResponse(
                i.id(), i.changeRequestId(), i.projectId(), i.currencyCode(), i.scopeImpact(),
                i.scheduleImpactDays(), i.estimateHoursImpact(),
                includeFinance ? i.laborCostImpact() : null,
                includeFinance ? i.directCostImpact() : null,
                includeFinance ? i.overheadImpact() : null,
                includeFinance ? i.revenueImpact() : null,
                includeFinance ? i.grossMarginImpact() : null,
                includeFinance ? i.pbtImpact() : null,
                includeFinance ? i.quoteAmountImpact() : null,
                i.riskImpact(), i.impactSummaryJson(), i.createdAt(), i.updatedAt());
    }
}
