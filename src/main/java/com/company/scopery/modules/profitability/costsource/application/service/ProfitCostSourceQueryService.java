package com.company.scopery.modules.profitability.costsource.application.service;

import com.company.scopery.modules.profitability.costsource.application.response.ProfitCostSourceResponse;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitCostSourceQueryService {
    private final ProfitCostSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitCostSourceQueryService(ProfitCostSourceRepository sources,
                                        ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitCostSourceResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return sources.findByProjectId(projectId).stream().map(ProfitCostSourceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitCostSourceResponse get(UUID projectId, UUID sourceId) {
        authorization.requireView(projectId);
        return ProfitCostSourceResponse.from(sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.costSourceNotFound(sourceId)));
    }
}
