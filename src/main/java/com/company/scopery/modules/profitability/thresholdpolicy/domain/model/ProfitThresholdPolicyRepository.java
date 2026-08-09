package com.company.scopery.modules.profitability.thresholdpolicy.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ProfitThresholdPolicyRepository {
    Optional<ProfitThresholdPolicy> findByProjectId(UUID projectId);
    ProfitThresholdPolicy save(ProfitThresholdPolicy policy);
}
