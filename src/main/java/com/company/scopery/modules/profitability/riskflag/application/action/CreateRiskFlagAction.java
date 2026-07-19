package com.company.scopery.modules.profitability.riskflag.application.action;

import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.riskflag.application.response.ProfitRiskFlagResponse;
import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlag;
import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlagRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateRiskFlagAction {
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitRiskFlagRepository riskFlags;
    private final ProfitabilityAuthorizationService authorization;

    public CreateRiskFlagAction(ProjectProfitabilityProfileRepository profiles,
                                ProfitRiskFlagRepository riskFlags,
                                ProfitabilityAuthorizationService authorization) {
        this.profiles = profiles;
        this.riskFlags = riskFlags;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRiskFlagResponse execute(
            UUID projectId,
            String reason,
            String impactType,
            BigDecimal amountAtRisk) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        ProfitRiskFlag created = ProfitRiskFlag.create(profile.workspaceId(), projectId, reason, impactType, amountAtRisk);
        return ProfitRiskFlagResponse.from(riskFlags.save(created));
    }
}
