package com.company.scopery.modules.profitability.threshold.application.response;
import com.company.scopery.modules.profitability.threshold.domain.model.ProfitThresholdPolicy;
import java.math.BigDecimal; import java.util.UUID;
public record ProfitThresholdPolicyResponse(UUID id, UUID projectId, BigDecimal healthyMarginPercent, BigDecimal watchMarginPercent,
        BigDecimal atRiskMarginPercent, BigDecimal lossRiskMarginPercent) {
    public static ProfitThresholdPolicyResponse from(ProfitThresholdPolicy p) {
        return new ProfitThresholdPolicyResponse(p.id(), p.projectId(), p.healthyMarginPercent(), p.watchMarginPercent(), p.atRiskMarginPercent(), p.lossRiskMarginPercent());
    }
}
