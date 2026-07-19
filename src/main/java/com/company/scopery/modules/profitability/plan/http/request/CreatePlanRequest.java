package com.company.scopery.modules.profitability.plan.http.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreatePlanRequest(
        @NotBlank String name,
        @NotBlank String planType,
        String planCode,
        String versionLabel,
        @NotBlank String currency,
        BigDecimal plannedRevenue,
        BigDecimal plannedCost,
        BigDecimal plannedProfit,
        BigDecimal plannedMarginPercent,
        BigDecimal baselineRevenue,
        BigDecimal baselineCost,
        BigDecimal baselineProfit,
        BigDecimal baselineMarginPercent,
        String assumptionNotes
) {}
