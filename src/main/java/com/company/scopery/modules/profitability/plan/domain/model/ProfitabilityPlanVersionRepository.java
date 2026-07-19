package com.company.scopery.modules.profitability.plan.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitabilityPlanVersionRepository {
    ProfitabilityPlanVersion save(ProfitabilityPlanVersion version);
    Optional<ProfitabilityPlanVersion> findById(UUID id);
    Optional<ProfitabilityPlanVersion> findByIdAndPlanId(UUID id, UUID planId);
    List<ProfitabilityPlanVersion> findByPlanId(UUID planId);
    int countByPlanId(UUID planId);
}
