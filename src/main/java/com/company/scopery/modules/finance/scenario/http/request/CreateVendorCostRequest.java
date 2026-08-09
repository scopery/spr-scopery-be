package com.company.scopery.modules.finance.scenario.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVendorCostRequest(
        UUID projectPhaseId,
        @NotBlank String vendorName,
        String description,
        @NotNull @DecimalMin("0") BigDecimal amount,
        @NotBlank String currencyCode
) {
}
