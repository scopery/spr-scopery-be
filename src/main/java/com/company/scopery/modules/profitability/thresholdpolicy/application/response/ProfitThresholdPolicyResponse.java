package com.company.scopery.modules.profitability.thresholdpolicy.application.response;

import com.company.scopery.modules.profitability.thresholdpolicy.domain.model.ProfitThresholdPolicy;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitThresholdPolicyResponse(
        UUID id,
        UUID projectId,
        BigDecimal healthyMarginPercent,
        BigDecimal watchMarginPercent,
        BigDecimal atRiskMarginPercent,
        BigDecimal lossRiskMarginPercent,
        String createdAt,
        String updatedAt
) {
    public static ProfitThresholdPolicyResponse from(ProfitThresholdPolicy p) {
        return new ProfitThresholdPolicyResponse(
                p.id(),
                p.projectId(),
                p.healthyMarginPercent(),
                p.watchMarginPercent(),
                p.atRiskMarginPercent(),
                p.lossRiskMarginPercent(),
                p.createdAt() != null ? p.createdAt().toString() : null,
                p.updatedAt() != null ? p.updatedAt().toString() : null
        );
    }
}
