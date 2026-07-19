package com.company.scopery.modules.profitability.costforecast.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitCostForecastRepository {
    ProfitCostForecast save(ProfitCostForecast forecast);
    Optional<ProfitCostForecast> findById(UUID id);
    List<ProfitCostForecast> findByProjectId(UUID projectId);
    List<ProfitCostForecast> findByProjectIdAndStatus(UUID projectId, String status);
}
