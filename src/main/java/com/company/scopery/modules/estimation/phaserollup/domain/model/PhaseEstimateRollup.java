package com.company.scopery.modules.estimation.phaserollup.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PhaseEstimateRollup(
        UUID id, UUID estimationRunId, UUID projectId, UUID projectPhaseId,
        int taskCount, int includedTaskCount, int unresolvedTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        String currencyCode, Instant createdAt
) {
    public static PhaseEstimateRollup create(
            UUID estimationRunId, UUID projectId, UUID projectPhaseId,
            int taskCount, int includedTaskCount, int unresolvedTaskCount,
            BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
            String currencyCode) {
        return new PhaseEstimateRollup(UUID.randomUUID(), estimationRunId, projectId, projectPhaseId,
                taskCount, includedTaskCount, unresolvedTaskCount,
                totalEstimateHours, totalLaborCost, totalBillingPreview, currencyCode, null);
    }
}
