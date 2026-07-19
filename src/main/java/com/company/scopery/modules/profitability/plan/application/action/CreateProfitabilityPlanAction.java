package com.company.scopery.modules.profitability.plan.application.action;

import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanResponse;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlan;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanRepository;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersion;
import com.company.scopery.modules.profitability.plan.domain.model.ProfitabilityPlanVersionRepository;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateProfitabilityPlanAction {
    private final ProfitabilityPlanRepository plans;
    private final ProfitabilityPlanVersionRepository planVersions;
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization;

    public CreateProfitabilityPlanAction(ProfitabilityPlanRepository plans,
                                         ProfitabilityPlanVersionRepository planVersions,
                                         ProjectProfitabilityProfileRepository profiles,
                                         ProfitabilityAuthorizationService authorization) {
        this.plans = plans;
        this.planVersions = planVersions;
        this.profiles = profiles;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitabilityPlanResponse execute(
            UUID projectId,
            String planCode,
            String name,
            String planType,
            String versionLabel,
            String currency,
            BigDecimal plannedRevenue,
            BigDecimal plannedCost,
            BigDecimal plannedProfit,
            BigDecimal plannedMarginPercent,
            String assumptionNotes) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        try {
            ProfitabilityPlan plan = plans.save(ProfitabilityPlan.create(
                    profile.workspaceId(), projectId, profile.id(), planCode, name, planType));
            ProfitabilityPlanVersion version = planVersions.save(ProfitabilityPlanVersion.create(
                    profile.workspaceId(), projectId, plan.id(),
                    1, versionLabel, currency,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null,
                    plannedRevenue, plannedCost, plannedProfit, plannedMarginPercent,
                    assumptionNotes));
            ProfitabilityPlan activated = plans.save(plan.activate(version.id()));
            return ProfitabilityPlanResponse.from(activated);
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.planInvalidStatus(null, ex.getMessage());
        }
    }
}
