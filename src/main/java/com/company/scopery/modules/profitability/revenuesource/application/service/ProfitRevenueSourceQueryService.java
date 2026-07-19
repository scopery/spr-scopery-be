package com.company.scopery.modules.profitability.revenuesource.application.service;

import com.company.scopery.modules.profitability.revenuesource.application.response.ProfitRevenueSourceResponse;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitRevenueSourceQueryService {
    private final ProfitRevenueSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitRevenueSourceQueryService(ProfitRevenueSourceRepository sources,
                                           ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitRevenueSourceResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return sources.findByProjectId(projectId).stream().map(ProfitRevenueSourceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitRevenueSourceResponse get(UUID projectId, UUID sourceId) {
        authorization.requireView(projectId);
        return ProfitRevenueSourceResponse.from(sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.revenueSourceNotFound(sourceId)));
    }
}
