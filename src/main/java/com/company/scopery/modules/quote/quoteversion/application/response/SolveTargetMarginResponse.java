package com.company.scopery.modules.quote.quoteversion.application.response;

import java.math.BigDecimal;

public record SolveTargetMarginResponse(
        BigDecimal costBase,
        BigDecimal targetMarginPercent,
        BigDecimal requiredContractValue,
        String currencyCode
) {}
