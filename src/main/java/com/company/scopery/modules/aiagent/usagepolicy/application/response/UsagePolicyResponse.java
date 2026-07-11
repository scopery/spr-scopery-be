package com.company.scopery.modules.aiagent.usagepolicy.application.response;

import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UsagePolicyResponse(
        UUID id,
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
        int priority,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static UsagePolicyResponse from(UsagePolicy policy) {
        return new UsagePolicyResponse(
                policy.id(),
                policy.code().value(),
                policy.name(),
                policy.targetType().name(),
                policy.targetId(),
                policy.maxRequestsPerPeriod(),
                policy.maxTokensPerPeriod(),
                policy.maxCostPerPeriod(),
                policy.maxConcurrentRequests(),
                policy.dailyBudget(),
                policy.period() != null ? policy.period().name() : null,
                policy.action().name(),
                policy.priority(),
                policy.description(),
                policy.status().name(),
                policy.createdAt(),
                policy.updatedAt()
        );
    }
}