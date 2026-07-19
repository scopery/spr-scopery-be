package com.company.scopery.modules.profitability.costsource.application.response;

import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSource;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitCostSourceResponse(
        UUID id,
        String sourceType,
        UUID sourceId,
        BigDecimal effortHours,
        BigDecimal rateAmount,
        BigDecimal amount,
        String currency,
        boolean includedInForecast,
        String status
) {
    public static ProfitCostSourceResponse from(ProfitCostSource s) {
        return new ProfitCostSourceResponse(
                s.id(), s.sourceType(), s.sourceId(), s.effortHours(), s.rateAmount(), s.amount(),
                s.currency(), s.includedInForecast(), s.status());
    }
}
