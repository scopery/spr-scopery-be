package com.company.scopery.modules.resourcecapacity.planning.application.response;

import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;

import java.math.BigDecimal;
import java.util.UUID;

public record ThresholdPolicyResponse(UUID id, UUID workspaceId, UUID projectId, BigDecimal underAllocatedPercent,
        BigDecimal healthyMinPercent, BigDecimal healthyMaxPercent, BigDecimal watchMaxPercent,
        BigDecimal overloadedPercent, BigDecimal criticalOverloadPercent, boolean enabled) {

    public static ThresholdPolicyResponse from(UtilizationThresholdPolicy p) {
        return new ThresholdPolicyResponse(p.id(), p.workspaceId(), p.projectId(),
                p.underAllocatedPercent(), p.healthyMinPercent(), p.healthyMaxPercent(),
                p.watchMaxPercent(), p.overloadedPercent(), p.criticalOverloadPercent(), p.enabled());
    }
}
