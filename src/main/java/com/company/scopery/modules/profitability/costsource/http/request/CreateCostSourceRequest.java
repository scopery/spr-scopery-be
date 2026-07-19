package com.company.scopery.modules.profitability.costsource.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCostSourceRequest(
        @NotBlank String sourceType,
        UUID sourceId,
        BigDecimal effortHours,
        BigDecimal rateAmount,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal amount,
        @NotBlank String currency,
        Boolean includedInForecast
) {}
