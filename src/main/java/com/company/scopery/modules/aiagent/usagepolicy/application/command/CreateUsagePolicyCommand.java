package com.company.scopery.modules.aiagent.usagepolicy.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateUsagePolicyCommand(
        String code,
        String name,
        String targetType,
        UUID targetId,
        Integer maxRequestsPerPeriod,
        Long maxTokensPerPeriod,
        BigDecimal maxCostPerPeriod,
        Integer maxConcurrentRequests,
        BigDecimal dailyBudget,
        String period,
        String action,
        Integer priority,
        String description
) {}