package com.company.scopery.modules.finance.scenario.application.response;

import com.company.scopery.modules.finance.scenario.domain.model.CustomCost;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomCostResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectPhaseId,
        String category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        String costDate,
        String status,
        String createdAt,
        String updatedAt
) {
    public static CustomCostResponse from(CustomCost c) {
        return new CustomCostResponse(
                c.id(),
                c.financeScenarioId(),
                c.projectPhaseId(),
                c.category(),
                c.name(),
                c.description(),
                c.amount(),
                c.currencyCode(),
                c.costDate() != null ? c.costDate().toString() : null,
                c.status() != null ? c.status().name() : null,
                c.createdAt() != null ? c.createdAt().toString() : null,
                c.updatedAt() != null ? c.updatedAt().toString() : null
        );
    }
}
