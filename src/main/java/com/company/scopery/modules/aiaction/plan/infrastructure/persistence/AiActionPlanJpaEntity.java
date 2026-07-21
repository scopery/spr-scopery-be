package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.PLAN)
public class AiActionPlanJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "plan_number", nullable = false)
    private int planNumber;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "policy_code", length = 100)
    private String policyCode;

    @Column(name = "policy_version", nullable = false)
    private int policyVersion;

    @Column(name = "plan_hash", length = 200)
    private String planHash;

    @Column(name = "context_hash", length = 200)
    private String contextHash;

    @Column(name = "source_state_hash", length = 200)
    private String sourceStateHash;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "execution_mode", length = 40)
    private String executionMode;

    @Column(name = "requires_confirmation", nullable = false)
    private boolean requiresConfirmation;

    @Column(name = "step_count", nullable = false)
    private int stepCount;

    @Column(name = "target_count", nullable = false)
    private int targetCount;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
