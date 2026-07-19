package com.company.scopery.modules.projectfinance.customcost.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateCustomCostRequest(
        UUID projectPhaseId,
        @NotBlank String category,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal amount,
        String currencyCode,
        LocalDate costDate
) {}
