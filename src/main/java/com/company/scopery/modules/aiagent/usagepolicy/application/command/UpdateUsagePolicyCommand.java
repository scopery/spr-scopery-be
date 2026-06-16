package com.company.scopery.modules.aiagent.usagepolicy.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateUsagePolicyCommand(
        UUID id,
        String name,
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