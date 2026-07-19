package com.company.scopery.modules.profitability.ratecard.http.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRateCardRequest(
        @NotBlank String rateCode,
        @NotBlank String name,
        @NotBlank String rateType,
        String roleName,
        UUID teamId,
        @NotBlank String currency,
        BigDecimal amountPerHour,
        BigDecimal amountPerDay
) {}
