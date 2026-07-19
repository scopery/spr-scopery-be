package com.company.scopery.modules.profitability.costsource.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitCostSourceRepository {
    ProfitCostSource save(ProfitCostSource source);
    List<ProfitCostSource> findByProjectId(UUID projectId);
    Optional<ProfitCostSource> findByIdAndProjectId(UUID id, UUID projectId);
}
