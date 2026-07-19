package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
        name = AiRecommendationTableNames.SUGGESTION,
        indexes = {
                @Index(name = "idx_ai_recommendation_suggestion_run_id",           columnList = "run_id"),
                @Index(name = "idx_ai_recommendation_suggestion_policy_id",        columnList = "policy_id"),
                @Index(name = "idx_ai_recommendation_suggestion_workspace_id",     columnList = "workspace_id"),
                @Index(name = "idx_ai_recommendation_suggestion_project_id",       columnList = "project_id"),
                @Index(name = "idx_ai_recommendation_suggestion_status",           columnList = "status"),
                @Index(name = "idx_ai_recommendation_suggestion_dedup_key",        columnList = "workspace_id,dedup_key"),
                @Index(name = "idx_ai_recommendation_suggestion_target_entity",    columnList = "workspace_id,target_entity_type,target_entity_id"),
                @Index(name = "idx_ai_recommendation_suggestion_expires_at",       columnList = "expires_at"),
                @Index(name = "idx_ai_recommendation_suggestion_severity",         columnList = "severity"),
                @Index(name = "idx_ai_recommendation_suggestion_pack_code",        columnList = "pack_code")
        }
)
public class AiSuggestionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "run_id")
    private UUID runId;

    @Column(name = "policy_id")
    private UUID policyId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "source_system", length = 100)
    private String sourceSystem;

    @Column(name = "legacy_phase21_suggestion_id")
    private UUID legacyPhase21SuggestionId;

    @Column(name = "pack_code", length = 100)
    private String packCode;

    @Column(name = "detector_code", length = 100)
    private String detectorCode;

    @Column(name = "suggestion_type", length = 100)
    private String suggestionType;

    @Column(name = "schema_code", length = 100)
    private String schemaCode;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "severity", nullable = false, length = 100)
    private String severity;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "target_entity_type", length = 100)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    @Column(name = "target_version_token", length = 255)
    private String targetVersionToken;

    @Column(name = "confidence_method", length = 100)
    private String confidenceMethod;

    @Column(name = "confidence_value", precision = 5, scale = 4)
    private BigDecimal confidenceValue;

    @Column(name = "confidence_label", length = 100)
    private String confidenceLabel;

    @Column(name = "risk_level", length = 100)
    private String riskLevel;

    @Column(name = "dedup_key", length = 500)
    private String dedupKey;

    @Column(name = "payload_hash", length = 255)
    private String payloadHash;

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "origin_conversation_id")
    private UUID originConversationId;

    @Column(name = "origin_message_id")
    private UUID originMessageId;

    @Column(name = "origin_turn_id")
    private UUID originTurnId;

    @Column(name = "supersedes_suggestion_id")
    private UUID supersedesSuggestionId;

    @Column(name = "superseded_by_suggestion_id")
    private UUID supersededBySuggestionId;

    @Column(name = "first_observed_at")
    private Instant firstObservedAt;

    @Column(name = "last_observed_at")
    private Instant lastObservedAt;

    @Column(name = "viewed_at")
    private Instant viewedAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "suppressed_at")
    private Instant suppressedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "stale_at")
    private Instant staleAt;

    @Column(name = "stale_reason_code", length = 100)
    private String staleReasonCode;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
