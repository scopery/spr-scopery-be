package com.company.scopery.modules.profitability.revenuesource.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitRevenueSourceRepository {
    ProfitRevenueSource save(ProfitRevenueSource source);
    List<ProfitRevenueSource> findByProjectId(UUID projectId);
    Optional<ProfitRevenueSource> findByIdAndProjectId(UUID id, UUID projectId);
}
