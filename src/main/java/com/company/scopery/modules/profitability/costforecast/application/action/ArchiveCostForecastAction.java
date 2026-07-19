package com.company.scopery.modules.profitability.costforecast.application.action;

import com.company.scopery.modules.profitability.costforecast.application.response.ProfitCostForecastResponse;
import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecastRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveCostForecastAction {
    private final ProfitCostForecastRepository forecasts;
    private final ProfitabilityAuthorizationService authorization;

    public ArchiveCostForecastAction(ProfitCostForecastRepository forecasts,
                                     ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitCostForecastResponse execute(UUID projectId, UUID forecastId) {
        authorization.requireUpdate(projectId);
        var forecast = forecasts.findById(forecastId)
                .orElseThrow(() -> ProfitabilityExceptions.costForecastNotFound(forecastId));
        return ProfitCostForecastResponse.from(forecasts.save(forecast.archive()));
    }
}
