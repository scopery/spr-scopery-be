package com.company.scopery.modules.finance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinanceScenarioCommand(
        UUID projectId,
        UUID workspaceId,
        UUID estimationRunId,
        String code,
        String name,
        String description,
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
) {
}
