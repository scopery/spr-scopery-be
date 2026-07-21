package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.STEP_EXECUTION)
public class AiActionStepExecutionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "step_id", nullable = false)
    private UUID stepId;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;

    @Column(name = "tool_code", nullable = false, length = 100)
    private String toolCode;

    @Column(name = "attempt", nullable = false)
    private int attempt;

    @Column(name = "idempotency_key", nullable = false, length = 500)
    private String idempotencyKey;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "safe_result_summary", columnDefinition = "TEXT")
    private String safeResultSummaryJson;

    @Column(name = "domain_result_ref", length = 200)
    private String domainResultRef;

    @Column(name = "result_version_token", length = 200)
    private String resultVersionToken;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "retryable")
    private Boolean retryable;

    @Column(name = "audit_ref", length = 200)
    private String auditRef;

    @Column(name = "outbox_ref", length = 200)
    private String outboxRef;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
