package com.company.scopery.modules.finance.scenario.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePhaseRevenueCommand(
        UUID projectId,
        UUID scenarioId,
        UUID projectPhaseId,
        BigDecimal plannedRevenue,
        BigDecimal revenuePercent
) {
}
