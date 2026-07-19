package com.company.scopery.modules.projectfinance.vendorcost.application.response;

import com.company.scopery.modules.projectfinance.vendorcost.domain.model.ProjectVendorCost;

import java.math.BigDecimal;
import java.util.UUID;

public record VendorCostResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        UUID projectPhaseId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode,
        String status
) {
    public static VendorCostResponse from(ProjectVendorCost c) {
        return new VendorCostResponse(
                c.id(), c.financeScenarioId(), c.projectId(), c.projectPhaseId(),
                c.vendorName(), c.description(), c.amount(), c.currencyCode(), c.status().name());
    }
}
