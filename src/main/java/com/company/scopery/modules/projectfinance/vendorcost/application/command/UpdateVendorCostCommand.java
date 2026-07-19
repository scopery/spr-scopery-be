package com.company.scopery.modules.projectfinance.vendorcost.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateVendorCostCommand(
        UUID projectId,
        UUID scenarioId,
        UUID costId,
        UUID projectPhaseId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode
) {}
