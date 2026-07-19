package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FinanceReportResponse(
        Object currentFinanceScenarioId,
        Boolean sourceAvailable,
        BigDecimal plannedRevenue,
        BigDecimal directCost,
        BigDecimal laborCost,
        BigDecimal customCost,
        BigDecimal vendorCost,
        BigDecimal contingency,
        BigDecimal overhead,
        BigDecimal budgetOfCosts,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        List<PhaseFinanceRowResponse> phaseFinanceBreakdown
) {
    public record PhaseFinanceRowResponse(
            UUID projectPhaseId,
            String phaseName,
            BigDecimal plannedRevenue,
            BigDecimal directCost,
            BigDecimal grossMargin,
            BigDecimal pbtPercent
    ) {}
}
