package com.company.scopery.modules.profitability.variance.application.response;

import com.company.scopery.modules.profitability.variance.domain.model.ProfitVariance;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitVarianceResponse(
        UUID id,
        String varianceType,
        BigDecimal fromAmount,
        BigDecimal toAmount,
        BigDecimal varianceAmount,
        BigDecimal variancePercent,
        String currency,
        String explanation,
        UUID sourceSnapshotId,
        Instant createdAt
) {
    public static ProfitVarianceResponse from(ProfitVariance v) {
        return new ProfitVarianceResponse(
                v.id(), v.varianceType(), v.fromAmount(), v.toAmount(),
                v.varianceAmount(), v.variancePercent(), v.currency(),
                v.explanation(), v.sourceSnapshotId(), v.createdAt());
    }
}
