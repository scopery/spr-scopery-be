package com.company.scopery.modules.profitability.thresholdpolicy.application.service;

import com.company.scopery.modules.profitability.thresholdpolicy.application.response.ProfitThresholdPolicyResponse;
import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicy;
import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfitThresholdPolicyQueryService {

    private final ProfitThresholdPolicyRepository policies;

    public ProfitThresholdPolicyQueryService(ProfitThresholdPolicyRepository policies) {
        this.policies = policies;
    }

    @Transactional(readOnly = true)
    public ProfitThresholdPolicyResponse getPolicy(UUID projectId) {
        return policies.findByProjectId(projectId)
                .map(ProfitThresholdPolicyResponse::from)
                .orElseGet(() -> ProfitThresholdPolicyResponse.from(ProfitThresholdPolicy.createDefault(projectId)));
    }
}
