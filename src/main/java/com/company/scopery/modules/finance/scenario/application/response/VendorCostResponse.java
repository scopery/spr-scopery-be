package com.company.scopery.modules.finance.scenario.application.response;

import com.company.scopery.modules.finance.scenario.domain.model.VendorCost;

import java.math.BigDecimal;
import java.util.UUID;

public record VendorCostResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectPhaseId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode,
        String status,
        String createdAt,
        String updatedAt
) {
    public static VendorCostResponse from(VendorCost v) {
        return new VendorCostResponse(
                v.id(),
                v.financeScenarioId(),
                v.projectPhaseId(),
                v.vendorName(),
                v.description(),
                v.amount(),
                v.currencyCode(),
                v.status() != null ? v.status().name() : null,
                v.createdAt() != null ? v.createdAt().toString() : null,
                v.updatedAt() != null ? v.updatedAt().toString() : null
        );
    }
}
