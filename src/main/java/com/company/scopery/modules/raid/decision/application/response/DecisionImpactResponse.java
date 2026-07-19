package com.company.scopery.modules.raid.decision.application.response;

import com.company.scopery.modules.raid.decision.domain.model.DecisionImpact;
import java.math.BigDecimal;
import java.util.UUID;

public record DecisionImpactResponse(
        UUID id,
        UUID decisionId,
        String scopeImpact,
        Integer scheduleImpactDays,
        BigDecimal estimateHoursImpact,
        BigDecimal costImpact,
        BigDecimal revenueImpact,
        BigDecimal marginImpact,
        String riskImpact,
        String deliverableImpact,
        String acceptanceImpact,
        Boolean financeMasked
) {
    public static DecisionImpactResponse from(DecisionImpact i) {
        return new DecisionImpactResponse(i.id(), i.decisionId(), i.scopeImpact(), i.scheduleImpactDays(),
                i.estimateHoursImpact(), i.costImpact(), i.revenueImpact(), i.marginImpact(),
                i.riskImpact(), i.deliverableImpact(), i.acceptanceImpact(), false);
    }

    public static DecisionImpactResponse fromMasked(DecisionImpact i) {
        return new DecisionImpactResponse(i.id(), i.decisionId(), i.scopeImpact(), i.scheduleImpactDays(),
                i.estimateHoursImpact(), null, null, null,
                i.riskImpact(), i.deliverableImpact(), i.acceptanceImpact(), true);
    }
}
