package com.company.scopery.modules.raid.decision.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertDecisionImpactCommand(
        UUID projectId,
        UUID decisionId,
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
