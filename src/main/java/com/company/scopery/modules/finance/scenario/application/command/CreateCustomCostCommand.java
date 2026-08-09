package com.company.scopery.modules.finance.scenario.application.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateCustomCostCommand(
        UUID projectId,
        UUID scenarioId,
        UUID projectPhaseId,
        String category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        Instant costDate
) {
}
