package com.company.scopery.modules.profitability.ratecard.application.service;

import com.company.scopery.modules.profitability.ratecard.application.response.ProfitRateCardResponse;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCardRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitRateCardQueryService {
    private final ProfitRateCardRepository repo;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitRateCardQueryService(ProfitRateCardRepository repo,
                                      ProfitabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitRateCardResponse> listByWorkspace(UUID workspaceId) {
        return repo.findByWorkspaceId(workspaceId).stream().map(ProfitRateCardResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProfitRateCardResponse> listByProject(UUID projectId) {
        authorization.requireView(projectId);
        return repo.findByProjectId(projectId).stream().map(ProfitRateCardResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitRateCardResponse get(UUID id) {
        return ProfitRateCardResponse.from(repo.findById(id)
                .orElseThrow(() -> ProfitabilityExceptions.rateCardNotFound(id)));
    }
}
