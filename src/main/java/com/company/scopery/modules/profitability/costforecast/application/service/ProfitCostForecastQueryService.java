package com.company.scopery.modules.profitability.costforecast.application.service;

import com.company.scopery.modules.profitability.costforecast.application.response.ProfitCostForecastResponse;
import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecastRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProfitCostForecastQueryService {
    private final ProfitCostForecastRepository forecasts;
    private final ProfitabilityAuthorizationService authorization;

    public ProfitCostForecastQueryService(ProfitCostForecastRepository forecasts,
                                          ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ProfitCostForecastResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return forecasts.findByProjectId(projectId).stream().map(ProfitCostForecastResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProfitCostForecastResponse get(UUID projectId, UUID forecastId) {
        authorization.requireView(projectId);
        return ProfitCostForecastResponse.from(forecasts.findById(forecastId)
                .orElseThrow(() -> ProfitabilityExceptions.costForecastNotFound(forecastId)));
    }
}
