package com.company.scopery.modules.profitability.plan.application.service;

import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanResponse;
import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanVersionResponse;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanRepository;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersionRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitabilityPlanQueryService {
    private final ProfitabilityPlanRepository plans;
    private final ProfitabilityPlanVersionRepository planVersions;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitabilityPlanQueryService(ProfitabilityPlanRepository plans,
                                         ProfitabilityPlanVersionRepository planVersions,
                                         ProfitabilityAuthorizationService authorization) {
        this.plans = plans;
        this.planVersions = planVersions;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitabilityPlanResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return plans.findByProjectId(projectId).stream().map(ProfitabilityPlanResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitabilityPlanResponse getPlan(UUID projectId, UUID planId) {
        authorization.requireView(projectId);
        return ProfitabilityPlanResponse.from(plans.findByIdAndProjectId(planId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.planNotFound(planId)));
    }

    @Transactional(readOnly = true)
    public List<ProfitabilityPlanVersionResponse> listVersions(UUID projectId, UUID planId) {
        authorization.requireView(projectId);
        plans.findByIdAndProjectId(planId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.planNotFound(planId));
        return planVersions.findByPlanId(planId).stream().map(ProfitabilityPlanVersionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitabilityPlanVersionResponse getVersion(UUID projectId, UUID planId, UUID versionId) {
        authorization.requireView(projectId);
        plans.findByIdAndProjectId(planId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.planNotFound(planId));
        return ProfitabilityPlanVersionResponse.from(planVersions.findByIdAndPlanId(versionId, planId)
                .orElseThrow(() -> ProfitabilityExceptions.planVersionNotFound(versionId)));
    }
}
