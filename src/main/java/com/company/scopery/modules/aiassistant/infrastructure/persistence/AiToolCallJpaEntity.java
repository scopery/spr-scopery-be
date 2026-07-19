package com.company.scopery.modules.aiassistant.infrastructure.persistence;

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
    name = AiAssistantTableNames.TOOL_CALL,
    indexes = {
        @Index(name = "idx_aiassistant_tool_call_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_aiassistant_tool_call_request_message_id", columnList = "request_message_id"),
        @Index(name = "idx_aiassistant_tool_call_status", columnList = "status"),
        @Index(name = "idx_aiassistant_tool_call_tool_code", columnList = "tool_code")
    }
)
public class AiToolCallJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "turn_id", nullable = false, updatable = false)
    private UUID turnId;

    @Column(name = "request_message_id", nullable = false, updatable = false)
    private UUID requestMessageId;

    @Column(name = "result_message_id")
    private UUID resultMessageId;

    @Column(name = "tool_code", nullable = false, length = 100)
    private String toolCode;

    @Column(name = "tool_version", nullable = false, length = 50)
    private String toolVersion;

    @Column(name = "handler_code", nullable = false, length = 100)
    private String handlerCode;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(name = "masked_arguments", columnDefinition = "TEXT")
    private String maskedArguments;

    @Column(name = "server_resolved_scope", columnDefinition = "TEXT")
    private String serverResolvedScope;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @Column(name = "retrieval_trace_id")
    private UUID retrievalTraceId;

    @Column(name = "result_count", nullable = false)
    private int resultCount;

    @Column(name = "truncated", nullable = false)
    private boolean truncated;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_summary_redacted", length = 1000)
    private String errorSummaryRedacted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
