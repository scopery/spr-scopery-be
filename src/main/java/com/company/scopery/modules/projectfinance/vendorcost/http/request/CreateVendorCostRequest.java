package com.company.scopery.modules.projectfinance.vendorcost.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVendorCostRequest(
        UUID projectPhaseId,
        String vendorName,
        @NotBlank String description,
        @NotNull BigDecimal amount,
        String currencyCode
) {}
