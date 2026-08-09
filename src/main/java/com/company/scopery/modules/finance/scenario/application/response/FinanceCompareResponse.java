package com.company.scopery.modules.finance.scenario.application.response;

import java.math.BigDecimal;

public record FinanceCompareResponse(
        FinanceScenarioResponse leftScenario,
        FinanceScenarioResponse rightScenario,
        FinanceSummaryResponse leftSummary,
        FinanceSummaryResponse rightSummary,
        BigDecimal deltaTotalDirectCost,
        BigDecimal deltaBudgetOfCosts,
        BigDecimal deltaPlannedRevenue,
        BigDecimal deltaGrossMargin,
        BigDecimal deltaGrossMarginPercent,
        BigDecimal deltaProfitBeforeTax,
        BigDecimal deltaPbtPercent
) {
}
