package com.company.scopery.modules.profitability.revenueforecast.application.service;

import com.company.scopery.modules.profitability.revenueforecast.application.response.ProfitRevenueForecastResponse;
import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecastRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitRevenueForecastQueryService {
    private final ProfitRevenueForecastRepository forecasts;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitRevenueForecastQueryService(ProfitRevenueForecastRepository forecasts,
                                              ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitRevenueForecastResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return forecasts.findByProjectId(projectId).stream().map(ProfitRevenueForecastResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitRevenueForecastResponse get(UUID projectId, UUID forecastId) {
        authorization.requireView(projectId);
        return ProfitRevenueForecastResponse.from(forecasts.findById(forecastId)
                .orElseThrow(() -> ProfitabilityExceptions.revenueForecastNotFound(forecastId)));
    }
}
