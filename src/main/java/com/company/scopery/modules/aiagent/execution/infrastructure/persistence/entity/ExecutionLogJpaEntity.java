package com.company.scopery.modules.aiagent.execution.infrastructure.persistence.entity;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAgentTableNames.EXECUTION_LOG,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_execution_log_request_id", columnNames = {"request_id"}),
    indexes = {
        @Index(name = "idx_aiagent_execution_log_request_id",        columnList = "request_id"),
        @Index(name = "idx_aiagent_execution_log_event_config_id",   columnList = "event_config_id"),
        @Index(name = "idx_aiagent_execution_log_event_definition_id", columnList = "event_definition_id"),
        @Index(name = "idx_aiagent_execution_log_agent_id",          columnList = "agent_id"),
        @Index(name = "idx_aiagent_execution_log_prompt_version_id", columnList = "prompt_version_id"),
        @Index(name = "idx_aiagent_execution_log_model_deployment_id", columnList = "model_deployment_id"),
        @Index(name = "idx_aiagent_execution_log_trigger_source",    columnList = "trigger_source"),
        @Index(name = "idx_aiagent_execution_log_status",            columnList = "status"),
        @Index(name = "idx_aiagent_execution_log_created_at",        columnList = "created_at"),
        @Index(name = "idx_aiagent_execution_log_completed_at",      columnList = "completed_at")
    }
)
public class ExecutionLogJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false, length = 150, updatable = false)
    private String requestId;

    @Column(name = "event_config_id", updatable = false)
    private UUID eventConfigId;

    @Column(name = "event_definition_id", updatable = false)
    private UUID eventDefinitionId;

    @Column(name = "agent_id", nullable = false, updatable = false)
    private UUID agentId;

    @Column(name = "prompt_version_id", nullable = false, updatable = false)
    private UUID promptVersionId;

    @Column(name = "model_deployment_id", nullable = false, updatable = false)
    private UUID modelDeploymentId;

    @Column(name = "trigger_source", nullable = false, length = 50, updatable = false)
    private String triggerSource;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "input_token_count")
    private Integer inputTokenCount;

    @Column(name = "output_token_count")
    private Integer outputTokenCount;

    @Column(name = "total_token_count")
    private Integer totalTokenCount;

    @Column(name = "estimated_cost", precision = 14, scale = 4)
    private BigDecimal estimatedCost;

    @Column(name = "provider_request_id", length = 255)
    private String providerRequestId;

    @Column(name = "error_code", length = 150)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "prompt_template_id", updatable = false)
    private UUID promptTemplateId;

    @Column(name = "provider_id", updatable = false)
    private UUID providerId;

    @Column(name = "model_id", updatable = false)
    private UUID modelId;

    @Column(name = "environment", length = 50, updatable = false)
    private String environment;

    @Column(name = "triggered_by_user_id", updatable = false)
    private UUID triggeredByUserId;

    @Column(name = "input_hash", length = 255, updatable = false)
    private String inputHash;

    @Column(name = "input_preview_json", columnDefinition = "TEXT", updatable = false)
    private String inputPreviewJson;

    @Column(name = "output_preview_json", columnDefinition = "TEXT")
    private String outputPreviewJson;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "trace_id", length = 100, updatable = false)
    private String traceId;

    @Column(name = "block_reason_code", length = 150)
    private String blockReasonCode;

}
