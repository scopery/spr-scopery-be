package com.company.scopery.modules.profitability.riskflag.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRiskFlagRequest(
        @NotBlank @Size(max = 500) String reason,
        @NotBlank @Size(max = 50) String impactType,
        BigDecimal amountAtRisk
) {}
