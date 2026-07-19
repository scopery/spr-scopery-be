package com.company.scopery.modules.profitability.plan.application.action;

import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanVersionResponse;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersionRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class FinalizePlanVersionAction {
    private final ProfitabilityPlanVersionRepository planVersions;
    private final ProfitabilityAuthorizationService authorization;

    public FinalizePlanVersionAction(ProfitabilityPlanVersionRepository planVersions,
                                     ProfitabilityAuthorizationService authorization) {
        this.planVersions = planVersions;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitabilityPlanVersionResponse execute(UUID projectId, UUID planId, UUID versionId) {
        authorization.requireUpdate(projectId);
        var version = planVersions.findByIdAndPlanId(versionId, planId)
                .orElseThrow(() -> ProfitabilityExceptions.planVersionNotFound(versionId));
        if (version.finalizedFlag()) {
            throw ProfitabilityExceptions.planVersionImmutable(versionId);
        }
        return ProfitabilityPlanVersionResponse.from(planVersions.save(version.finalize(null)));
    }
}
