package com.company.scopery.modules.profitability.variance.application.service;

import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import com.company.scopery.modules.profitability.variance.application.response.ProfitVarianceResponse;
import com.company.scopery.modules.profitability.variance.domain.model.ProfitVarianceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitVarianceQueryService {
    private final ProfitVarianceRepository variances;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitVarianceQueryService(ProfitVarianceRepository variances,
                                      ProfitabilityAuthorizationService authorization) {
        this.variances = variances;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitVarianceResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return variances.findByProjectId(projectId).stream().map(ProfitVarianceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitVarianceResponse get(UUID projectId, UUID varianceId) {
        authorization.requireView(projectId);
        return ProfitVarianceResponse.from(variances.findById(varianceId)
                .orElseThrow(() -> ProfitabilityExceptions.snapshotNotFound(varianceId)));
    }
}
