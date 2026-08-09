package com.company.scopery.modules.finance.scenario.http.request;

import java.math.BigDecimal;

public record UpdateFinanceScenarioRequest(
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
) {
}
