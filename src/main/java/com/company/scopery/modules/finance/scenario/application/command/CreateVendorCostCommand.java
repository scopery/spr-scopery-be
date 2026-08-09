package com.company.scopery.modules.finance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVendorCostCommand(
        UUID projectId,
        UUID scenarioId,
        UUID projectPhaseId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode
) {
}
