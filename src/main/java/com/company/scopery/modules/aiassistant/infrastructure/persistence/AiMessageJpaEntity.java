package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
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
@Table(
    name = AiAssistantTableNames.MESSAGE,
    indexes = {
        @Index(name = "idx_aiassistant_message_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_aiassistant_message_turn_id", columnList = "turn_id"),
        @Index(name = "idx_aiassistant_message_status", columnList = "status"),
        @Index(name = "idx_aiassistant_message_idempotency_key", columnList = "idempotency_key")
    }
)
public class AiMessageJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "turn_id", nullable = false, updatable = false)
    private UUID turnId;

    @Column(name = "parent_message_id")
    private UUID parentMessageId;

    @Column(name = "idempotency_key", length = 200)
    private String idempotencyKey;

    @Column(name = "sequence_in_conversation", nullable = false)
    private int sequenceInConversation;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "content_format", nullable = false, length = 50)
    private String contentFormat;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "response_mode", length = 50)
    private String responseMode;

    @Column(name = "model_provider", length = 100)
    private String modelProvider;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(name = "model_deployment", length = 200)
    private String modelDeployment;

    @Column(name = "prompt_profile_code", length = 100)
    private String promptProfileCode;

    @Column(name = "input_token_count", nullable = false)
    private int inputTokenCount;

    @Column(name = "output_token_count", nullable = false)
    private int outputTokenCount;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "finish_reason", length = 100)
    private String finishReason;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_summary_redacted", length = 1000)
    private String errorSummaryRedacted;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "cancel_requested_at")
    private Instant cancelRequestedAt;

    @Column(name = "cancel_requested_by")
    private UUID cancelRequestedBy;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
