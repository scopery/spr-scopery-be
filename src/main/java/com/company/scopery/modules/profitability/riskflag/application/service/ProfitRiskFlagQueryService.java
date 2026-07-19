package com.company.scopery.modules.profitability.riskflag.application.service;

import com.company.scopery.modules.profitability.riskflag.application.response.ProfitRiskFlagResponse;
import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlagRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitRiskFlagQueryService {
    private final ProfitRiskFlagRepository riskFlags;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitRiskFlagQueryService(ProfitRiskFlagRepository riskFlags,
                                      ProfitabilityAuthorizationService authorization) {
        this.riskFlags = riskFlags;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitRiskFlagResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return riskFlags.findByProjectId(projectId).stream().map(ProfitRiskFlagResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitRiskFlagResponse getById(UUID projectId, UUID riskFlagId) {
        authorization.requireView(projectId);
        return ProfitRiskFlagResponse.from(riskFlags.findById(riskFlagId)
                .orElseThrow(() -> ProfitabilityExceptions.riskFlagNotFound(riskFlagId)));
    }
}
