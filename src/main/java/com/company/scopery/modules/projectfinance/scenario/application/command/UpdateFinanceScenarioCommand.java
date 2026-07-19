package com.company.scopery.modules.projectfinance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateFinanceScenarioCommand(
        UUID projectId,
        UUID scenarioId,
        String name,
        String description,
        BigDecimal plannedRevenue,
        String revenueSplitMethod,
        String contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        String overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson
) {}
