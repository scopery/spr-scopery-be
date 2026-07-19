package com.company.scopery.modules.projectbaseline.changeimpact.http.request;
import java.math.BigDecimal;
public record UpdateChangeImpactRequest(
        String currencyCode, String scopeImpact, Integer scheduleImpactDays,
        BigDecimal estimateHoursImpact, BigDecimal laborCostImpact, BigDecimal directCostImpact,
        BigDecimal overheadImpact, BigDecimal revenueImpact, BigDecimal grossMarginImpact,
        BigDecimal pbtImpact, BigDecimal quoteAmountImpact, String riskImpact, String impactSummaryJson) {}
