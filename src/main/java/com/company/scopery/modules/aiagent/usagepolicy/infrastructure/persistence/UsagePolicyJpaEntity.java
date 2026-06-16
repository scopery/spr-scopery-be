package com.company.scopery.modules.aiagent.usagepolicy.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAgentTableNames.USAGE_POLICY,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_usage_policy_code", columnNames = {"code"}),
    indexes = {
        @Index(name = "idx_aiagent_usage_policy_code",        columnList = "code"),
        @Index(name = "idx_aiagent_usage_policy_target_type", columnList = "target_type"),
        @Index(name = "idx_aiagent_usage_policy_target_id",   columnList = "target_id"),
        @Index(name = "idx_aiagent_usage_policy_status",      columnList = "status")
    }
)
public class UsagePolicyJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "target_type", nullable = false, length = 50, updatable = false)
    private String targetType;

    @Column(name = "target_id", updatable = false)
    private UUID targetId;

    @Column(name = "max_requests_per_period")
    private Integer maxRequestsPerPeriod;

    @Column(name = "max_tokens_per_period")
    private Long maxTokensPerPeriod;

    @Column(name = "max_cost_per_period", precision = 19, scale = 6)
    private BigDecimal maxCostPerPeriod;

    @Column(name = "max_concurrent_requests")
    private Integer maxConcurrentRequests;

    @Column(name = "daily_budget", precision = 19, scale = 6)
    private BigDecimal dailyBudget;

    @Column(name = "period", length = 50)
    private String period;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}