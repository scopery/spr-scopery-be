package com.company.scopery.modules.profitability.variance.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVarianceRequest(
        @NotBlank String varianceType,
        @NotNull BigDecimal fromAmount,
        @NotNull BigDecimal toAmount,
        @NotNull BigDecimal varianceAmount,
        BigDecimal variancePercent,
        @NotBlank String currency,
        String explanation,
        UUID sourceSnapshotId
) {}
