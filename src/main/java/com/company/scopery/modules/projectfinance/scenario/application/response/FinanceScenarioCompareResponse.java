package com.company.scopery.modules.projectfinance.scenario.application.response;

import com.company.scopery.modules.projectfinance.summary.application.response.FinanceSummaryResponse;
import java.util.UUID;

public record FinanceScenarioCompareResponse(
        UUID projectId,
        UUID leftScenarioId,
        UUID rightScenarioId,
        FinanceScenarioResponse leftScenario,
        FinanceScenarioResponse rightScenario,
        FinanceSummaryResponse leftSummary,
        FinanceSummaryResponse rightSummary,
        FinanceSummaryDeltaResponse delta
) {
    public record FinanceSummaryDeltaResponse(
            java.math.BigDecimal plannedRevenueDelta,
            java.math.BigDecimal budgetOfCostsDelta,
            java.math.BigDecimal grossMarginDelta,
            java.math.BigDecimal grossMarginPercentDelta,
            java.math.BigDecimal profitBeforeTaxDelta,
            java.math.BigDecimal totalEstimateHoursDelta
    ) {}
}
