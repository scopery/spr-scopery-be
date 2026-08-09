package com.company.scopery.modules.finance.scenario.http.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFinanceScenarioRequest(
        @NotBlank String name,
        @NotBlank String code,
        String description,
        UUID estimationRunId,
        @NotBlank String currencyCode,
        @NotNull @DecimalMin("0") BigDecimal plannedRevenue,
        String revenueSplitMethod,
        @NotBlank String contingencyMethod,
        BigDecimal contingencyPercent,
        BigDecimal contingencyFixedAmount,
        @NotBlank String overheadMethod,
        BigDecimal overheadPercent,
        BigDecimal overheadFixedAmount,
        BigDecimal targetMarginPercent,
        String assumptionsJson,
        Boolean markAsCurrent
) {
}
