package com.company.scopery.modules.profitability.revenueforecast.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRevenueForecastRequest(
        @NotBlank String forecastType,
        @NotBlank String currency,
        @NotNull BigDecimal forecastAmount,
        BigDecimal confidencePercent,
        @NotNull LocalDate forecastDate,
        String assumptionNotes
) {}
