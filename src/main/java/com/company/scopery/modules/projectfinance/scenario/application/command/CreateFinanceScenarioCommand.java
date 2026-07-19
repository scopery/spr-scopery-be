package com.company.scopery.modules.projectfinance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinanceScenarioCommand(
        UUID projectId,
        String name,
        String description,
        String code,
        UUID estimationRunId,
        String currencyCode,
        BigDecimal plannedRevenue,
        String revenueSplitMethod,
        String contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        String overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        boolean markAsCurrent
) {}
