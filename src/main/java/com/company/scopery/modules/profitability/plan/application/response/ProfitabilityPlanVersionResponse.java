package com.company.scopery.modules.profitability.plan.application.response;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersion;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitabilityPlanVersionResponse(
        UUID id,
        UUID profitabilityPlanId,
        int versionNumber,
        String versionLabel,
        String currency,
        BigDecimal baselineRevenue,
        BigDecimal baselineCost,
        BigDecimal baselineProfit,
        BigDecimal plannedRevenue,
        BigDecimal plannedCost,
        BigDecimal plannedProfit,
        String assumptionNotes,
        boolean finalizedFlag,
        String status
) {
    public static ProfitabilityPlanVersionResponse from(ProfitabilityPlanVersion v) {
        return new ProfitabilityPlanVersionResponse(
                v.id(), v.profitabilityPlanId(), v.versionNumber(), v.versionLabel(),
                v.currency(),
                v.baselineRevenue(), v.baselineCost(), v.baselineProfit(),
                v.plannedRevenue(), v.plannedCost(), v.plannedProfit(),
                v.assumptionNotes(), v.finalizedFlag(), v.status());
    }
}
