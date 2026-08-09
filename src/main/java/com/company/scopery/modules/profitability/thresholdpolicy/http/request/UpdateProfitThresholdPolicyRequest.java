package com.company.scopery.modules.profitability.thresholdpolicy.http.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProfitThresholdPolicyRequest(
        @NotNull BigDecimal healthyMarginPercent,
        @NotNull BigDecimal watchMarginPercent,
        @NotNull BigDecimal atRiskMarginPercent,
        @NotNull BigDecimal lossRiskMarginPercent
) {}
