package com.company.scopery.modules.finance.scenario.http.request;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateCustomCostRequest(
        String category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        Instant costDate
) {
}
