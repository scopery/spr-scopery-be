package com.company.scopery.modules.finance.scenario.application.response;

import com.company.scopery.modules.finance.scenario.domain.model.PhaseFinance;

import java.math.BigDecimal;
import java.util.UUID;

public record PhaseFinanceResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectPhaseId,
        String phaseNameSnapshot,
        Integer phaseOrder,
        BigDecimal estimateHours,
        BigDecimal laborCost,
        BigDecimal customCost,
        BigDecimal vendorCost,
        BigDecimal contingencyAmount,
        BigDecimal directCost,
        BigDecimal overheadAmount,
        BigDecimal budgetOfCosts,
        BigDecimal plannedRevenue,
        BigDecimal revenuePercent,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent
) {
    public static PhaseFinanceResponse from(PhaseFinance p) {
        return new PhaseFinanceResponse(
                p.id(),
                p.financeScenarioId(),
                p.projectPhaseId(),
                p.phaseNameSnapshot(),
                p.phaseOrder(),
                p.estimateHours(),
                p.laborCost(),
                p.customCost(),
                p.vendorCost(),
                p.contingencyAmount(),
                p.directCost(),
                p.overheadAmount(),
                p.budgetOfCosts(),
                p.plannedRevenue(),
                p.revenuePercent(),
                p.grossMargin(),
                p.grossMarginPercent(),
                p.profitBeforeTax(),
                p.pbtPercent()
        );
    }
}
