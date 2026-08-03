package com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
        name = AiMappingTableNames.MAPPING_SUGGESTION,
        indexes = {
                @Index(name = "idx_ai_mapping_suggestion_run_id", columnList = "run_id"),
                @Index(name = "idx_ai_mapping_suggestion_source", columnList = "source_type, source_id"),
                @Index(name = "idx_ai_mapping_suggestion_review", columnList = "review_status, confidence_band")
        }
)
public class MappingSuggestionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "run_id", nullable = false, updatable = false)
    private UUID runId;

    @Column(name = "source_type", nullable = false, length = 50, updatable = false)
    private String sourceType;

    @Column(name = "source_id", nullable = false, updatable = false)
    private UUID sourceId;

    @Column(name = "source_version")
    private Integer sourceVersion;

    @Column(name = "source_summary_hash", length = 64)
    private String sourceSummaryHash;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "target_version")
    private Integer targetVersion;

    @Column(name = "target_summary_hash", length = 64)
    private String targetSummaryHash;

    @Column(name = "relation_type", nullable = false, length = 50, updatable = false)
    private String relationType;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "retrieval_score", precision = 5, scale = 4)
    private BigDecimal retrievalScore;

    @Column(name = "ai_score", precision = 5, scale = 4)
    private BigDecimal aiScore;

    @Column(name = "final_score", precision = 5, scale = 4)
    private BigDecimal finalScore;

    @Column(name = "second_best_score", precision = 5, scale = 4)
    private BigDecimal secondBestScore;

    @Column(name = "score_margin", precision = 5, scale = 4)
    private BigDecimal scoreMargin;

    @Column(name = "confidence_band", length = 20)
    private String confidenceBand;

    @Column(name = "decision", nullable = false, length = 20)
    private String decision;

    @Column(name = "reason_codes_json", columnDefinition = "TEXT")
    private String reasonCodesJson;

    @Column(name = "evidence_json", columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(name = "warnings_json", columnDefinition = "TEXT")
    private String warningsJson;

    @Column(name = "coverage_json", columnDefinition = "TEXT")
    private String coverageJson;

    @Column(name = "review_status", nullable = false, length = 20)
    private String reviewStatus;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}
