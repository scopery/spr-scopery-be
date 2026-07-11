package com.company.scopery.modules.aiagent.usagepolicy.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateUsagePolicyRequest(
        @NotBlank @Size(max = 255) String name,
        @Positive Integer maxRequestsPerPeriod,
        @Positive Long maxTokensPerPeriod,
        @Positive BigDecimal maxCostPerPeriod,
        @Positive Integer maxConcurrentRequests,
        @Positive BigDecimal dailyBudget,
        String period,
        @NotBlank String action,
        @Positive Integer priority,
        String description
) {}
