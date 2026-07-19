package com.company.scopery.modules.projectfinance.summary.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectFinanceSummary(
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
        String formulaVersion,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectFinanceSummary create(
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
            String formulaVersion) {
        return new ProjectFinanceSummary(
                UUID.randomUUID(), financeScenarioId, projectId, currencyCode,
                nz(totalEstimateHours), nz(totalLaborCost), nz(totalCustomCost), nz(totalVendorCost),
                nz(totalContingency), nz(totalDirectCost), nz(totalOverhead), nz(budgetOfCosts),
                nz(plannedRevenue), grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent,
                averageCostRate, formulaVersion, null, null);
    }

    public ProjectFinanceSummary withId(UUID existingId) {
        return new ProjectFinanceSummary(
                existingId, financeScenarioId, projectId, currencyCode, totalEstimateHours,
                totalLaborCost, totalCustomCost, totalVendorCost, totalContingency, totalDirectCost,
                totalOverhead, budgetOfCosts, plannedRevenue, grossMargin, grossMarginPercent,
                profitBeforeTax, pbtPercent, averageCostRate, formulaVersion, createdAt, updatedAt);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
