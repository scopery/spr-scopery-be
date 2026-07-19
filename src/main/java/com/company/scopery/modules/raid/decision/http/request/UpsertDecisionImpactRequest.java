package com.company.scopery.modules.raid.decision.http.request;

import java.math.BigDecimal;

public record UpsertDecisionImpactRequest(
        String scopeImpact,
        Integer scheduleImpactDays,
        BigDecimal estimateHoursImpact,
        BigDecimal costImpact,
        BigDecimal revenueImpact,
        BigDecimal marginImpact,
        String riskImpact,
        String deliverableImpact,
        String acceptanceImpact
) {}
