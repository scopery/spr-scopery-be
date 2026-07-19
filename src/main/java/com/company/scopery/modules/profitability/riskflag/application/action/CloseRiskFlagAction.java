package com.company.scopery.modules.profitability.riskflag.application.action;

import com.company.scopery.modules.profitability.riskflag.application.response.ProfitRiskFlagResponse;
import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlagRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CloseRiskFlagAction {
    private final ProfitRiskFlagRepository riskFlags;
    private final ProfitabilityAuthorizationService authorization;

    public CloseRiskFlagAction(ProfitRiskFlagRepository riskFlags,
                                ProfitabilityAuthorizationService authorization) {
        this.riskFlags = riskFlags;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRiskFlagResponse execute(UUID projectId, UUID riskFlagId) {
        authorization.requireUpdate(projectId);
        var existing = riskFlags.findById(riskFlagId)
                .orElseThrow(() -> ProfitabilityExceptions.riskFlagNotFound(riskFlagId));
        try {
            return ProfitRiskFlagResponse.from(riskFlags.save(existing.close()));
        } catch (IllegalStateException ex) {
            throw ProfitabilityExceptions.riskFlagInvalidStatus(riskFlagId, existing.status());
        }
    }
}
