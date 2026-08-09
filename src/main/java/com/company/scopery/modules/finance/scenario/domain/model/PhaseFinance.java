package com.company.scopery.modules.finance.scenario.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PhaseFinance(
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
        BigDecimal pbtPercent,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {

    public static PhaseFinance create(
            UUID financeScenarioId,
            UUID projectPhaseId,
            String phaseNameSnapshot,
            Integer phaseOrder,
            BigDecimal estimateHours,
            BigDecimal laborCost) {
        return new PhaseFinance(
                UUID.randomUUID(),
                financeScenarioId,
                projectPhaseId,
                phaseNameSnapshot,
                phaseOrder != null ? phaseOrder : 0,
                estimateHours != null ? estimateHours : BigDecimal.ZERO,
                laborCost != null ? laborCost : BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                laborCost != null ? laborCost : BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                null,
                null
        );
    }

    public PhaseFinance withCalculatedValues(
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
            BigDecimal pbtPercent) {
        return new PhaseFinance(
                id, financeScenarioId, projectPhaseId, phaseNameSnapshot, phaseOrder,
                estimateHours, laborCost, customCost, vendorCost, contingencyAmount,
                directCost, overheadAmount, budgetOfCosts, plannedRevenue, revenuePercent,
                grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent,
                version, createdAt, updatedAt);
    }
}
