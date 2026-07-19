package com.company.scopery.modules.projectfinance.customcost.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        LocalDate costDate
) {}
