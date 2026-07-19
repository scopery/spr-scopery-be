package com.company.scopery.modules.profitability.ratecard.http.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateRateCardRequest(
        @NotBlank String name,
        String roleName,
        @NotBlank String currency,
        BigDecimal amountPerHour,
        BigDecimal amountPerDay
) {}
