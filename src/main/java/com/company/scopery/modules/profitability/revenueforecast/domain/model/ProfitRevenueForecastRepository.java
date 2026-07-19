package com.company.scopery.modules.profitability.revenueforecast.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfitRevenueForecastRepository {
    ProfitRevenueForecast save(ProfitRevenueForecast forecast);
    Optional<ProfitRevenueForecast> findById(UUID id);
    List<ProfitRevenueForecast> findByProjectId(UUID projectId);
    List<ProfitRevenueForecast> findByProjectIdAndStatus(UUID projectId, String status);
}
