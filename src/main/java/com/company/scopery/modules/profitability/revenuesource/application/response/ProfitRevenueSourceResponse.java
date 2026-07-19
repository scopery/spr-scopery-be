package com.company.scopery.modules.profitability.revenuesource.application.response;

import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSource;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitRevenueSourceResponse(
        UUID id,
        String sourceType,
        UUID sourceId,
        BigDecimal amount,
        String currency,
        boolean includedInForecast,
        String confidence,
        String status
) {
    public static ProfitRevenueSourceResponse from(ProfitRevenueSource s) {
        return new ProfitRevenueSourceResponse(
                s.id(), s.sourceType(), s.sourceId(), s.amount(), s.currency(),
                s.includedInForecast(), s.confidence(), s.status());
    }
}
