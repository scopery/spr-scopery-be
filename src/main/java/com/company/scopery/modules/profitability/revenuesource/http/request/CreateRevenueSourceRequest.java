package com.company.scopery.modules.profitability.revenuesource.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRevenueSourceRequest(
        @NotBlank String sourceType,
        UUID sourceId,
        @NotNull @DecimalMin(value = "0.0001", message = "amount must be positive") BigDecimal amount,
        @NotBlank String currency,
        Boolean includedInForecast,
        String confidence
) {}
