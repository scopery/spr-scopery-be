package com.company.scopery.modules.profitability.profile.application.response;
import java.math.BigDecimal;
public record ProfitabilitySummaryResponse(String currency, BigDecimal forecastRevenue, BigDecimal forecastCost, BigDecimal forecastProfit, BigDecimal forecastMarginPercent, String profitabilityStatus) {}
