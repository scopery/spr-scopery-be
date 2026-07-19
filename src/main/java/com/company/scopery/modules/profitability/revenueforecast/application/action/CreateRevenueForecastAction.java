package com.company.scopery.modules.profitability.revenueforecast.application.action;

import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.revenueforecast.application.response.ProfitRevenueForecastResponse;
import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecast;
import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecastRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class CreateRevenueForecastAction {
    private final ProfitRevenueForecastRepository forecasts;
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization;

    public CreateRevenueForecastAction(ProfitRevenueForecastRepository forecasts,
                                       ProjectProfitabilityProfileRepository profiles,
                                       ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.profiles = profiles;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRevenueForecastResponse execute(
            UUID projectId,
            String forecastType,
            String currency,
            BigDecimal forecastAmount,
            BigDecimal confidencePercent,
            LocalDate forecastDate,
            String assumptionNotes) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        try {
            return ProfitRevenueForecastResponse.from(forecasts.save(ProfitRevenueForecast.create(
                    profile.workspaceId(), projectId, profile.id(),
                    forecastType, currency, forecastAmount, confidencePercent, forecastDate, assumptionNotes)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidRevenueSource(ex.getMessage());
        }
    }
}
