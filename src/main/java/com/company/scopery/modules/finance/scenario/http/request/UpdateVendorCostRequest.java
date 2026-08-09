package com.company.scopery.modules.finance.scenario.http.request;

import java.math.BigDecimal;

public record UpdateVendorCostRequest(
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode
) {
}
