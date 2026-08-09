package com.company.scopery.modules.finance.scenario.domain.model;

import com.company.scopery.modules.finance.scenario.domain.enums.CostLineStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VendorCost(
        UUID id,
        UUID financeScenarioId,
        UUID projectPhaseId,
        String vendorName,
        String description,
        BigDecimal amount,
        String currencyCode,
        CostLineStatus status,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {

    public static VendorCost create(
            UUID financeScenarioId,
            UUID projectPhaseId,
            String vendorName,
            String description,
            BigDecimal amount,
            String currencyCode) {
        return new VendorCost(
                UUID.randomUUID(),
                financeScenarioId,
                projectPhaseId,
                vendorName,
                description,
                amount != null ? amount : BigDecimal.ZERO,
                currencyCode,
                CostLineStatus.ACTIVE,
                null,
                null,
                null
        );
    }

    public VendorCost archive() {
        return new VendorCost(
                id, financeScenarioId, projectPhaseId, vendorName, description,
                amount, currencyCode, CostLineStatus.ARCHIVED,
                version, createdAt, updatedAt);
    }
}
