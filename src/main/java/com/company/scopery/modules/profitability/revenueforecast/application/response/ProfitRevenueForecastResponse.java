package com.company.scopery.modules.profitability.revenueforecast.application.response;

import com.company.scopery.modules.profitability.revenueforecast.domain.model.ProfitRevenueForecast;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProfitRevenueForecastResponse(
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
    public static ProfitRevenueForecastResponse from(ProfitRevenueForecast f) {
        return new ProfitRevenueForecastResponse(
                f.id(), f.forecastType(), f.currency(), f.forecastAmount(),
                f.confidencePercent(), f.forecastDate(), f.assumptionNotes(),
                f.generatedBy(), f.status());
    }
}
