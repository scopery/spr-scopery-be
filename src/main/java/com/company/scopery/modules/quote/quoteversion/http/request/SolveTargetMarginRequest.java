package com.company.scopery.modules.quote.quoteversion.http.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolveTargetMarginRequest(
        BigDecimal costBase,
        @NotNull BigDecimal targetMarginPercent,
        String currencyCode
) {}
