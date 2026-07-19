package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
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
        name = AiRecommendationTableNames.RUN,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_run_idempotency",
                columnNames = {"workspace_id", "project_id", "idempotency_key"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_run_policy_id",      columnList = "policy_id"),
                @Index(name = "idx_ai_recommendation_run_workspace_id",   columnList = "workspace_id"),
                @Index(name = "idx_ai_recommendation_run_project_id",     columnList = "project_id"),
                @Index(name = "idx_ai_recommendation_run_status",         columnList = "status"),
                @Index(name = "idx_ai_recommendation_run_idempotency_key",columnList = "workspace_id,project_id,idempotency_key")
        }
)
public class AiRecommendationRunJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "requested_by")
    private UUID requestedBy;

    @Column(name = "trigger_type", nullable = false, length = 100)
    private String triggerType;

    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;

    @Column(name = "request_hash", length = 255)
    private String requestHash;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "requested_pack_codes", columnDefinition = "TEXT")
    private String requestedPackCodes;

    @Column(name = "detector_codes", columnDefinition = "TEXT")
    private String detectorCodes;

    @Column(name = "origin_conversation_id")
    private UUID originConversationId;

    @Column(name = "origin_message_id")
    private UUID originMessageId;

    @Column(name = "origin_turn_id")
    private UUID originTurnId;

    @Column(name = "detector_count", nullable = false)
    private int detectorCount;

    @Column(name = "candidate_count", nullable = false)
    private int candidateCount;

    @Column(name = "persisted_count", nullable = false)
    private int persistedCount;

    @Column(name = "deduplicated_count", nullable = false)
    private int deduplicatedCount;

    @Column(name = "suppressed_count", nullable = false)
    private int suppressedCount;

    @Column(name = "discarded_count", nullable = false)
    private int discardedCount;

    @Column(name = "failed_detector_count", nullable = false)
    private int failedDetectorCount;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_summary_redacted", columnDefinition = "TEXT")
    private String errorSummaryRedacted;

    @Column(name = "trace_id", length = 255)
    private String traceId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
