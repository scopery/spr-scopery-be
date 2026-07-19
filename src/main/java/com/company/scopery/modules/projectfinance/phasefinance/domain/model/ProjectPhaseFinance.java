package com.company.scopery.modules.projectfinance.phasefinance.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectPhaseFinance(
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
        BigDecimal pbtPercent,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectPhaseFinance create(
            UUID financeScenarioId,
            UUID projectId,
            UUID projectPhaseId,
            String phaseNameSnapshot,
            Integer phaseOrder,
            String currencyCode,
            BigDecimal estimateHours,
            BigDecimal laborCost) {
        BigDecimal zero = BigDecimal.ZERO;
        return new ProjectPhaseFinance(
                UUID.randomUUID(), financeScenarioId, projectId, projectPhaseId,
                phaseNameSnapshot, phaseOrder, currencyCode,
                nz(estimateHours), nz(laborCost), zero, zero, zero, zero, zero, zero, zero, null,
                null, null, null, null, null, null);
    }

    public ProjectPhaseFinance withCosts(
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
        return new ProjectPhaseFinance(
                id, financeScenarioId, projectId, projectPhaseId, phaseNameSnapshot, phaseOrder,
                currencyCode, estimateHours, laborCost, nz(customCost), nz(vendorCost),
                nz(contingencyAmount), nz(directCost), nz(overheadAmount), nz(budgetOfCosts),
                nz(plannedRevenue), revenuePercent, grossMargin, grossMarginPercent,
                profitBeforeTax, pbtPercent, createdAt, updatedAt);
    }

    public ProjectPhaseFinance withRevenue(BigDecimal plannedRevenue, BigDecimal revenuePercent) {
        return new ProjectPhaseFinance(
                id, financeScenarioId, projectId, projectPhaseId, phaseNameSnapshot, phaseOrder,
                currencyCode, estimateHours, laborCost, customCost, vendorCost, contingencyAmount,
                directCost, overheadAmount, budgetOfCosts, nz(plannedRevenue), revenuePercent,
                grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent, createdAt, updatedAt);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
