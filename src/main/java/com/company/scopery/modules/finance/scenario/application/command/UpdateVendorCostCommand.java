package com.company.scopery.modules.finance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateVendorCostCommand(
        UUID projectId,
        UUID scenarioId,
        UUID costId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode
) {
}
