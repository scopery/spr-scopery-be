package com.company.scopery.modules.estimation.wbsrollup.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WbsEstimateRollup(
        UUID id, UUID estimationRunId, UUID projectId, UUID wbsNodeId, UUID parentWbsNodeId,
        int depth, int taskCount, int includedTaskCount, int unresolvedTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        String currencyCode, Instant createdAt
) {
    public static WbsEstimateRollup create(
            UUID estimationRunId, UUID projectId, UUID wbsNodeId, UUID parentWbsNodeId, int depth,
            int taskCount, int includedTaskCount, int unresolvedTaskCount,
            BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
            String currencyCode) {
        return new WbsEstimateRollup(UUID.randomUUID(), estimationRunId, projectId, wbsNodeId, parentWbsNodeId,
                depth, taskCount, includedTaskCount, unresolvedTaskCount,
                totalEstimateHours, totalLaborCost, totalBillingPreview, currencyCode, null);
    }
}
