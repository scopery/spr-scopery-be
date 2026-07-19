package com.company.scopery.modules.projectfinance.customcost.application.response;

import com.company.scopery.modules.projectfinance.customcost.domain.model.ProjectCustomCost;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CustomCostResponse(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        UUID projectPhaseId,
        String category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        LocalDate costDate,
        String status
) {
    public static CustomCostResponse from(ProjectCustomCost c) {
        return new CustomCostResponse(
                c.id(), c.financeScenarioId(), c.projectId(), c.projectPhaseId(),
                c.category().name(), c.name(), c.description(), c.amount(), c.currencyCode(),
                c.costDate(), c.status().name());
    }
}
