package com.company.scopery.modules.estimation.projectsummary.application.response;

import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import java.math.BigDecimal; import java.util.UUID;

public record ProjectEstimateSummaryResponse(
        UUID id, UUID estimationRunId, UUID projectId,
        int totalTaskCount, int includedTaskCount, int excludedTaskCount,
        int unestimatedTaskCount, int unresolvedRoleTaskCount, int unresolvedRateTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        BigDecimal averageCostRate, BigDecimal averageBillingRate,
        String currencyCode, int warningCount
) {
    public static ProjectEstimateSummaryResponse from(ProjectEstimateSummary s) {
        return new ProjectEstimateSummaryResponse(s.id(), s.estimationRunId(), s.projectId(),
                s.totalTaskCount(), s.includedTaskCount(), s.excludedTaskCount(),
                s.unestimatedTaskCount(), s.unresolvedRoleTaskCount(), s.unresolvedRateTaskCount(),
                s.totalEstimateHours(), s.totalLaborCost(), s.totalBillingPreview(),
                s.averageCostRate(), s.averageBillingRate(), s.currencyCode(), s.warningCount());
    }
}
