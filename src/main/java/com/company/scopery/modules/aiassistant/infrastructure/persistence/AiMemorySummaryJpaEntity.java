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
    name = AiAssistantTableNames.MEMORY_SUMMARY,
    indexes = {
        @Index(name = "idx_aiassistant_memory_summary_conversation_id", columnList = "conversation_id"),
        @Index(name = "idx_aiassistant_memory_summary_status", columnList = "status"),
        @Index(name = "idx_aiassistant_memory_summary_summary_version", columnList = "conversation_id, summary_version")
    }
)
public class AiMemorySummaryJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "conversation_id", nullable = false, updatable = false)
    private UUID conversationId;

    @Column(name = "summary_version", nullable = false)
    private int summaryVersion;

    @Column(name = "strategy_code", nullable = false, length = 100)
    private String strategyCode;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "covered_through_message_sequence", nullable = false)
    private int coveredThroughMessageSequence;

    @Column(name = "source_message_count", nullable = false)
    private int sourceMessageCount;

    @Column(name = "estimated_token_count", nullable = false)
    private int estimatedTokenCount;

    @Column(name = "summary_text", nullable = false, columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "permission_signature", length = 500)
    private String permissionSignature;

    @Column(name = "summary_hash", nullable = false, length = 64)
    private String summaryHash;

    @Column(name = "model_provider", length = 100)
    private String modelProvider;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(name = "prompt_profile_code", length = 100)
    private String promptProfileCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "invalidated_at")
    private Instant invalidatedAt;

    @Column(name = "invalidation_reason_code", length = 100)
    private String invalidationReasonCode;
}
