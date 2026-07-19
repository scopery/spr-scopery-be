package com.company.scopery.modules.projectfinance.scenario.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinanceScenarioRequest(
        @NotBlank String name,
        String description,
        String code,
        @NotNull UUID estimationRunId,
        String currencyCode,
        BigDecimal plannedRevenue,
        String revenueSplitMethod,
        @Valid PolicyRequest contingency,
        @Valid PolicyRequest overhead,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        Boolean markAsCurrent
) {
    public record PolicyRequest(String method, BigDecimal percent, BigDecimal fixedAmount) {}
}
