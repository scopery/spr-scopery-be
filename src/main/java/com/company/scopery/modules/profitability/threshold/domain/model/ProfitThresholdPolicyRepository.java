package com.company.scopery.modules.profitability.threshold.domain.model;
import java.util.*; import java.util.UUID;
public interface ProfitThresholdPolicyRepository {
    ProfitThresholdPolicy save(ProfitThresholdPolicy p);
    Optional<ProfitThresholdPolicy> findByProjectId(UUID projectId);
}
