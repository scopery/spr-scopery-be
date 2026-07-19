package com.company.scopery.modules.estimation.phaserollup.application.response;

import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import java.math.BigDecimal; import java.util.UUID;

public record PhaseEstimateRollupResponse(
        UUID id, UUID estimationRunId, UUID projectId, UUID projectPhaseId,
        int taskCount, int includedTaskCount, int unresolvedTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        String currencyCode
) {
    public static PhaseEstimateRollupResponse from(PhaseEstimateRollup r) {
        return new PhaseEstimateRollupResponse(r.id(), r.estimationRunId(), r.projectId(), r.projectPhaseId(),
                r.taskCount(), r.includedTaskCount(), r.unresolvedTaskCount(),
                r.totalEstimateHours(), r.totalLaborCost(), r.totalBillingPreview(), r.currencyCode());
    }
}
