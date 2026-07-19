package com.company.scopery.modules.projectfinance.phasefinance.application.response;

import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;

import java.math.BigDecimal;
import java.util.UUID;

public record PhaseFinanceResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        UUID projectPhaseId,
        String phaseNameSnapshot,
        Integer phaseOrder,
        String currencyCode,
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
    public static PhaseFinanceResponse from(ProjectPhaseFinance p) {
        return new PhaseFinanceResponse(
                p.id(), p.financeScenarioId(), p.projectId(), p.projectPhaseId(), p.phaseNameSnapshot(),
                p.phaseOrder(), p.currencyCode(), p.estimateHours(), p.laborCost(), p.customCost(),
                p.vendorCost(), p.contingencyAmount(), p.directCost(), p.overheadAmount(),
                p.budgetOfCosts(), p.plannedRevenue(), p.revenuePercent(), p.grossMargin(),
                p.grossMarginPercent(), p.profitBeforeTax(), p.pbtPercent());
    }
}
