package com.company.scopery.modules.profitability.summary.application.response;

import com.company.scopery.modules.profitability.summary.domain.model.ProfitabilitySummary;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitabilitySummaryResponse(
        UUID id,
        UUID projectId,
        String currency,
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        String healthStatus,
        String updatedAt
) {
    public static ProfitabilitySummaryResponse from(ProfitabilitySummary s) {
        return new ProfitabilitySummaryResponse(
                s.id(),
                s.projectId(),
                s.currency(),
                s.totalRevenue(),
                s.totalCost(),
                s.grossMargin(),
                s.grossMarginPercent(),
                s.profitBeforeTax(),
                s.pbtPercent(),
                s.healthStatus().name(),
                s.updatedAt() != null ? s.updatedAt().toString() : null
        );
    }
}
