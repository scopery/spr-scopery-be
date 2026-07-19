package com.company.scopery.modules.profitability.ratecard.application.response;

import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCard;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitRateCardResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String rateCode,
        String name,
        String rateType,
        String roleName,
        UUID teamId,
        String currency,
        BigDecimal amountPerHour,
        BigDecimal amountPerDay,
        String status
) {
    public static ProfitRateCardResponse from(ProfitRateCard rc) {
        return new ProfitRateCardResponse(
                rc.id(), rc.workspaceId(), rc.projectId(), rc.rateCode(), rc.name(),
                rc.rateType(), rc.roleName(), rc.teamId(), rc.currency(),
                rc.amountPerHour(), rc.amountPerDay(), rc.status());
    }
}
