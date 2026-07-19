package com.company.scopery.modules.profitability.adjustment.application.response;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustment;
import java.math.BigDecimal; import java.util.UUID;
public record ProfitAdjustmentResponse(UUID id, String adjustmentType, BigDecimal amount, String reason, String status) {
    public static ProfitAdjustmentResponse from(ProfitAdjustment a) {
        return new ProfitAdjustmentResponse(a.id(), a.adjustmentType(), a.amount(), a.reason(), a.status());
    }
}
