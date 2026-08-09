package com.company.scopery.modules.finance.scenario.domain.model;

import com.company.scopery.modules.finance.scenario.domain.enums.CostLineStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CustomCost(
        UUID id,
        UUID financeScenarioId,
        UUID projectPhaseId,
        String category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        Instant costDate,
        CostLineStatus status,
        Integer version,
        Instant createdAt,
        Instant updatedAt
) {

    public static CustomCost create(
            UUID financeScenarioId,
            UUID projectPhaseId,
            String category,
            String name,
            String description,
            BigDecimal amount,
            String currencyCode,
            Instant costDate) {
        return new CustomCost(
                UUID.randomUUID(),
                financeScenarioId,
                projectPhaseId,
                category,
                name,
                description,
                amount != null ? amount : BigDecimal.ZERO,
                currencyCode,
                costDate,
                CostLineStatus.ACTIVE,
                null,
                null,
                null
        );
    }

    public CustomCost archive() {
        return new CustomCost(
                id, financeScenarioId, projectPhaseId, category, name, description,
                amount, currencyCode, costDate, CostLineStatus.ARCHIVED,
                version, createdAt, updatedAt);
    }
}
