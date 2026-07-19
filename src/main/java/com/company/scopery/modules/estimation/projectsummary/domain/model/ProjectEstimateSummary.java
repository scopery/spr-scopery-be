package com.company.scopery.modules.estimation.projectsummary.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectEstimateSummary(
        UUID id, UUID estimationRunId, UUID projectId,
        int totalTaskCount, int includedTaskCount, int excludedTaskCount,
        int unestimatedTaskCount, int unresolvedRoleTaskCount, int unresolvedRateTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        BigDecimal averageCostRate, BigDecimal averageBillingRate,
        String currencyCode, int warningCount, Instant createdAt
) {
    public static ProjectEstimateSummary create(
            UUID estimationRunId, UUID projectId,
            int totalTaskCount, int includedTaskCount, int excludedTaskCount,
            int unestimatedTaskCount, int unresolvedRoleTaskCount, int unresolvedRateTaskCount,
            BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
            BigDecimal averageCostRate, BigDecimal averageBillingRate,
            String currencyCode, int warningCount) {
        return new ProjectEstimateSummary(UUID.randomUUID(), estimationRunId, projectId,
                totalTaskCount, includedTaskCount, excludedTaskCount,
                unestimatedTaskCount, unresolvedRoleTaskCount, unresolvedRateTaskCount,
                totalEstimateHours, totalLaborCost, totalBillingPreview,
                averageCostRate, averageBillingRate, currencyCode, warningCount, null);
    }
}
