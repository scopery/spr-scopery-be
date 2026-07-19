package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EstimationReportResponse(
        Object currentEstimationRunId,
        Boolean sourceAvailable,
        BigDecimal totalEstimateHours,
        BigDecimal totalLaborCost,
        BigDecimal totalBillingPreview,
        Long unestimatedTaskCount,
        Long unresolvedRoleTaskCount,
        Long unresolvedRateTaskCount,
        List<PhaseEstimateRowResponse> estimateByPhase,
        List<WbsEstimateRowResponse> estimateByWbs,
        Map<String, BigDecimal> estimateByCostRole
) {
    public record PhaseEstimateRowResponse(
            UUID projectPhaseId,
            BigDecimal totalEstimateHours,
            BigDecimal totalLaborCost,
            BigDecimal totalBillingPreview
    ) {}

    public record WbsEstimateRowResponse(
            UUID wbsNodeId,
            BigDecimal totalEstimateHours,
            BigDecimal totalLaborCost,
            BigDecimal totalBillingPreview
    ) {}
}
