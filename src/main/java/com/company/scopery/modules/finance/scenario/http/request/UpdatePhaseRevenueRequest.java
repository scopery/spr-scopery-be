package com.company.scopery.modules.finance.scenario.http.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePhaseRevenueRequest(
        @NotNull BigDecimal plannedRevenue,
        @NotNull BigDecimal revenuePercent
) {
}
