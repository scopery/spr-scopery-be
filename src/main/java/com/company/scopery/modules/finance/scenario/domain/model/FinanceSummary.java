package com.company.scopery.modules.finance.scenario.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FinanceSummary(
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
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {

    public static FinanceSummary create(UUID scenarioId, UUID projectId, String currencyCode) {
        return new FinanceSummary(
                UUID.randomUUID(),
                scenarioId,
                projectId,
                currencyCode,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "v1",
                null,
                null,
                null
        );
    }

    public FinanceSummary recalculate(
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
            BigDecimal averageCostRate) {
        return new FinanceSummary(
                id, financeScenarioId, projectId, currencyCode,
                totalEstimateHours, totalLaborCost, totalCustomCost, totalVendorCost,
                totalContingency, totalDirectCost, totalOverhead, budgetOfCosts,
                plannedRevenue, grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent,
                averageCostRate, formulaVersion, version, createdAt, updatedAt);
    }
}
