package com.company.scopery.modules.profitability.costforecast.application.action;

import com.company.scopery.modules.profitability.costforecast.application.response.ProfitCostForecastResponse;
import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecast;
import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecastRepository;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class CreateCostForecastAction {
    private final ProfitCostForecastRepository forecasts;
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization;

    public CreateCostForecastAction(ProfitCostForecastRepository forecasts,
                                    ProjectProfitabilityProfileRepository profiles,
                                    ProfitabilityAuthorizationService authorization) {
        this.forecasts = forecasts;
        this.profiles = profiles;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitCostForecastResponse execute(
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
            return ProfitCostForecastResponse.from(forecasts.save(ProfitCostForecast.create(
                    profile.workspaceId(), projectId, profile.id(),
                    forecastType, currency, forecastAmount, confidencePercent, forecastDate, assumptionNotes)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidCostSource(ex.getMessage());
        }
    }
}
