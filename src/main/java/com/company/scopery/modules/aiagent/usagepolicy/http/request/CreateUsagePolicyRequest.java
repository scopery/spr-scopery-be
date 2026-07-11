package com.company.scopery.modules.aiagent.usagepolicy.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateUsagePolicyRequest(
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[A-Za-z0-9_]+$",
                message = "Code must contain only letters, numbers, and underscores")
        String code,
        @NotBlank @Size(max = 255) String name,
        @NotBlank String targetType,
        UUID targetId,
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
