package com.company.scopery.modules.profitability.variance.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitVarianceRepository {
    ProfitVariance save(ProfitVariance variance);
    Optional<ProfitVariance> findById(UUID id);
    List<ProfitVariance> findByProjectId(UUID projectId);
}
