package com.company.scopery.modules.aiagent.usagepolicy.application.response;

import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UsagePolicyDetailResponse(
        UUID id,
        String code,
        String name,
        String targetType,
        UUID targetId,
        String targetName,
        Integer maxRequestsPerPeriod,
        Long maxTokensPerPeriod,
        BigDecimal maxCostPerPeriod,
        Integer maxConcurrentRequests,
        BigDecimal dailyBudget,
        String environment,
        Integer maxRequestsPerMinute,
        Integer maxRequestsPerDay,
        Integer maxTokensPerRequest,
        Integer maxTokensPerDay,
        BigDecimal maxEstimatedCostPerDay,
        String allowedEventDefinitionIds,
        String blockedEventDefinitionIds,
        String period,
        String action,
        int priority,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static UsagePolicyDetailResponse from(UsagePolicy policy, String targetName) {
        return new UsagePolicyDetailResponse(
                policy.id(),
                policy.code().value(),
                policy.name(),
                policy.targetType().name(),
                policy.targetId(),
                targetName,
                policy.maxRequestsPerPeriod(),
                policy.maxTokensPerPeriod(),
                policy.maxCostPerPeriod(),
                policy.maxConcurrentRequests(),
                policy.dailyBudget(),
                policy.environment(),
                policy.maxRequestsPerMinute(),
                policy.maxRequestsPerDay(),
                policy.maxTokensPerRequest(),
                policy.maxTokensPerDay(),
                policy.maxEstimatedCostPerDay(),
                policy.allowedEventDefinitionIds(),
                policy.blockedEventDefinitionIds(),
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
