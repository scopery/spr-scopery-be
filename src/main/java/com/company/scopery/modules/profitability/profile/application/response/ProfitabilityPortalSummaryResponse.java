package com.company.scopery.modules.profitability.profile.application.response;

import java.math.BigDecimal;

/**
 * Portal-safe profitability summary — omits raw revenue/cost line items and detailed amounts
 * unless {@code portalVisibility} is FULL.
 */
public record ProfitabilityPortalSummaryResponse(
        String currency,
        String profitabilityStatus,
        BigDecimal forecastMarginPercent,
        boolean marginVisible,
        boolean amountsVisible,
        BigDecimal forecastRevenue,
        BigDecimal forecastCost,
        BigDecimal forecastProfit
) {}
