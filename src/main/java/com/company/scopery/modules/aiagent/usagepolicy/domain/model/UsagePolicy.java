package com.company.scopery.modules.aiagent.usagepolicy.domain.model;

import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyAction;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyPeriod;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject.UsagePolicyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class UsagePolicy {

    private final UUID id;
    private final UsagePolicyCode code;
    private String name;
    private final UsagePolicyTargetType targetType;
    private final UUID targetId;
    private Integer maxRequestsPerPeriod;
    private Long maxTokensPerPeriod;
    private BigDecimal maxCostPerPeriod;
    private Integer maxConcurrentRequests;
    private BigDecimal dailyBudget;
    private UsagePolicyPeriod period;
    private UsagePolicyAction action;
    private int priority;
    private String description;
    private UsagePolicyStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private UsagePolicy(UUID id, UsagePolicyCode code, String name,
                        UsagePolicyTargetType targetType, UUID targetId,
                        Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                        BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                        BigDecimal dailyBudget, UsagePolicyPeriod period,
                        UsagePolicyAction action, int priority, String description,
                        UsagePolicyStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.targetType = targetType;
        this.targetId = targetId;
        this.maxRequestsPerPeriod = maxRequestsPerPeriod;
        this.maxTokensPerPeriod = maxTokensPerPeriod;
        this.maxCostPerPeriod = maxCostPerPeriod;
        this.maxConcurrentRequests = maxConcurrentRequests;
        this.dailyBudget = dailyBudget;
        this.period = period;
        this.action = action;
        this.priority = priority;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UsagePolicy create(UsagePolicyCode code, String name,
                                     UsagePolicyTargetType targetType, UUID targetId,
                                     Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                                     BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                                     BigDecimal dailyBudget, UsagePolicyPeriod period,
                                     UsagePolicyAction action, int priority, String description) {
        validateName(name);
        Instant now = Instant.now();
        return new UsagePolicy(UUID.randomUUID(), code, name, targetType, targetId,
                maxRequestsPerPeriod, maxTokensPerPeriod, maxCostPerPeriod, maxConcurrentRequests,
                dailyBudget, period, action, priority, description,
                UsagePolicyStatus.INACTIVE, now, now);
    }

    public static UsagePolicy reconstitute(UUID id, UsagePolicyCode code, String name,
                                           UsagePolicyTargetType targetType, UUID targetId,
                                           Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                                           BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                                           BigDecimal dailyBudget, UsagePolicyPeriod period,
                                           UsagePolicyAction action, int priority, String description,
                                           UsagePolicyStatus status, Instant createdAt, Instant updatedAt) {
        return new UsagePolicy(id, code, name, targetType, targetId,
                maxRequestsPerPeriod, maxTokensPerPeriod, maxCostPerPeriod, maxConcurrentRequests,
                dailyBudget, period, action, priority, description, status, createdAt, updatedAt);
    }

    public void update(String name, Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                       BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                       BigDecimal dailyBudget, UsagePolicyPeriod period,
                       UsagePolicyAction action, int priority, String description) {
        validateName(name);
        this.name = name;
        this.maxRequestsPerPeriod = maxRequestsPerPeriod;
        this.maxTokensPerPeriod = maxTokensPerPeriod;
        this.maxCostPerPeriod = maxCostPerPeriod;
        this.maxConcurrentRequests = maxConcurrentRequests;
        this.dailyBudget = dailyBudget;
        this.period = period;
        this.action = action;
        this.priority = priority;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == UsagePolicyStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated usage policy cannot be activated again");
        }
        this.status = UsagePolicyStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = UsagePolicyStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Usage policy name is required");
        }
    }

    public UUID id() { return id; }
    public UsagePolicyCode code() { return code; }
    public String name() { return name; }
    public UsagePolicyTargetType targetType() { return targetType; }
    public UUID targetId() { return targetId; }
    public Integer maxRequestsPerPeriod() { return maxRequestsPerPeriod; }
    public Long maxTokensPerPeriod() { return maxTokensPerPeriod; }
    public BigDecimal maxCostPerPeriod() { return maxCostPerPeriod; }
    public Integer maxConcurrentRequests() { return maxConcurrentRequests; }
    public BigDecimal dailyBudget() { return dailyBudget; }
    public UsagePolicyPeriod period() { return period; }
    public UsagePolicyAction action() { return action; }
    public int priority() { return priority; }
    public String description() { return description; }
    public UsagePolicyStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
