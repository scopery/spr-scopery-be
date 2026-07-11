package com.company.scopery.modules.aiagent.execution.domain.model;

import java.math.BigDecimal;

public record UsageAggregate(
        long requestCount,
        long totalTokenCount,
        BigDecimal estimatedCost
) {}
