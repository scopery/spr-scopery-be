package com.company.scopery.modules.profitability.plan.application.response;

import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlan;

import java.util.UUID;

public record ProfitabilityPlanResponse(
        UUID id,
        UUID projectId,
        String planCode,
        String name,
        String planType,
        String status,
        UUID currentVersionId
) {
    public static ProfitabilityPlanResponse from(ProfitabilityPlan p) {
        return new ProfitabilityPlanResponse(
                p.id(), p.projectId(), p.planCode(), p.name(),
                p.planType(), p.status(), p.currentVersionId());
    }
}
