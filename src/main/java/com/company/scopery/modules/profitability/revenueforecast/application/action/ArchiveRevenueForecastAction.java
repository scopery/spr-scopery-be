package com.company.scopery.modules.profitability.revenueforecast.application.action;

import com.company.scopery.modules.profitability.revenueforecast.application.response.ProfitRevenueForecastResponse;
import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecastRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveRevenueForecastAction {
    private final ProfitRevenueForecastRepository forecasts;
    private final ProfitabilityAuthorizationService authorization;

    public ArchiveRevenueForecastAction(ProfitRevenueForecastRepository forecasts,
                                        ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRevenueForecastResponse execute(UUID projectId, UUID forecastId) {
        authorization.requireUpdate(projectId);
        var forecast = forecasts.findById(forecastId)
                .orElseThrow(() -> ProfitabilityExceptions.revenueForecastNotFound(forecastId));
        return ProfitRevenueForecastResponse.from(forecasts.save(forecast.archive()));
    }
}
