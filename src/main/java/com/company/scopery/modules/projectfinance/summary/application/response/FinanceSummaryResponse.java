package com.company.scopery.modules.projectfinance.summary.application.response;

import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;

import java.math.BigDecimal;
import java.util.UUID;

public record FinanceSummaryResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        String currencyCode,
        BigDecimal totalEstimateHours,
        BigDecimal totalLaborCost,
        BigDecimal totalCustomCost,
        BigDecimal totalVendorCost,
        BigDecimal totalContingency,
        BigDecimal totalDirectCost,
        BigDecimal totalOverhead,
        BigDecimal budgetOfCosts,
        BigDecimal plannedRevenue,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        BigDecimal averageCostRate,
        String formulaVersion
) {
    public static FinanceSummaryResponse from(ProjectFinanceSummary s) {
        return new FinanceSummaryResponse(
                s.id(), s.financeScenarioId(), s.projectId(), s.currencyCode(),
                s.totalEstimateHours(), s.totalLaborCost(), s.totalCustomCost(), s.totalVendorCost(),
                s.totalContingency(), s.totalDirectCost(), s.totalOverhead(), s.budgetOfCosts(),
                s.plannedRevenue(), s.grossMargin(), s.grossMarginPercent(), s.profitBeforeTax(),
                s.pbtPercent(), s.averageCostRate(), s.formulaVersion());
    }
}
