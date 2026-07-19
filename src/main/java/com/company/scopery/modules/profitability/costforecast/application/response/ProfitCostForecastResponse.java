package com.company.scopery.modules.profitability.costforecast.application.response;

import com.company.scopery.modules.profitability.costforecast.domain.model.ProfitCostForecast;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProfitCostForecastResponse(
        UUID id,
        String forecastType,
        String currency,
        BigDecimal forecastAmount,
        BigDecimal confidencePercent,
        LocalDate forecastDate,
        String assumptionNotes,
        String generatedBy,
        String status
) {
    public static ProfitCostForecastResponse from(ProfitCostForecast f) {
        return new ProfitCostForecastResponse(
                f.id(), f.forecastType(), f.currency(), f.forecastAmount(),
                f.confidencePercent(), f.forecastDate(), f.assumptionNotes(),
                f.generatedBy(), f.status());
    }
}
