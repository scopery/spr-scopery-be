package com.company.scopery.modules.estimation.wbsrollup.application.response;

import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollup;
import java.math.BigDecimal; import java.util.UUID;

public record WbsEstimateRollupResponse(
        UUID id, UUID estimationRunId, UUID projectId, UUID wbsNodeId, UUID parentWbsNodeId,
        int depth, int taskCount, int includedTaskCount, int unresolvedTaskCount,
        BigDecimal totalEstimateHours, BigDecimal totalLaborCost, BigDecimal totalBillingPreview,
        String currencyCode
) {
    public static WbsEstimateRollupResponse from(WbsEstimateRollup r) {
        return new WbsEstimateRollupResponse(r.id(), r.estimationRunId(), r.projectId(), r.wbsNodeId(),
                r.parentWbsNodeId(), r.depth(), r.taskCount(), r.includedTaskCount(), r.unresolvedTaskCount(),
                r.totalEstimateHours(), r.totalLaborCost(), r.totalBillingPreview(), r.currencyCode());
    }
}
