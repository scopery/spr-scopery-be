package com.company.scopery.modules.finance.scenario.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateCustomCostRequest(
        UUID projectPhaseId,
        @NotBlank String category,
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0") BigDecimal amount,
        @NotBlank String currencyCode,
        Instant costDate
) {
}
