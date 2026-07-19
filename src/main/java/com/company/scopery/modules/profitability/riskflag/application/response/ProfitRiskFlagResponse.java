package com.company.scopery.modules.profitability.riskflag.application.response;

import com.company.scopery.modules.profitability.riskflag.domain.model.ProfitRiskFlag;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitRiskFlagResponse(
        UUID id,
        UUID projectId,
        String reason,
        String impactType,
        BigDecimal amountAtRisk,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitRiskFlagResponse from(ProfitRiskFlag f) {
        return new ProfitRiskFlagResponse(
                f.id(),
                f.projectId(),
                f.reason(),
                f.impactType(),
                f.amountAtRisk(),
                f.status(),
                f.createdAt(),
                f.updatedAt());
    }
}
