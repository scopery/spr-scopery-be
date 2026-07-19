package com.company.scopery.modules.profitability.plan.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitabilityPlanRepository {
    ProfitabilityPlan save(ProfitabilityPlan plan);
    Optional<ProfitabilityPlan> findById(UUID id);
    Optional<ProfitabilityPlan> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProfitabilityPlan> findByProjectId(UUID projectId);
}
