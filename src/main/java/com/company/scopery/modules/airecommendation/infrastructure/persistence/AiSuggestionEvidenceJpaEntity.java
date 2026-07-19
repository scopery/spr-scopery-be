package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(
        name = AiRecommendationTableNames.EVIDENCE,
        indexes = {
                @Index(name = "idx_ai_recommendation_evidence_suggestion_id", columnList = "suggestion_id"),
                @Index(name = "idx_ai_recommendation_evidence_source",        columnList = "suggestion_id,source_type,source_ref_id")
        }
)
public class AiSuggestionEvidenceJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;

    @Column(name = "evidence_type", nullable = false, length = 100)
    private String evidenceType;

    @Column(name = "support_strength", length = 100)
    private String supportStrength;

    @Column(name = "aiassistant_citation_id")
    private UUID aiassistantCitationId;

    @Column(name = "knowledge_chunk_id")
    private UUID knowledgeChunkId;

    @Column(name = "retrieval_trace_id")
    private UUID retrievalTraceId;

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "source_ref_id")
    private UUID sourceRefId;

    @Column(name = "source_version_ref_id")
    private UUID sourceVersionRefId;

    @Column(name = "field_path", length = 500)
    private String fieldPath;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "quoted_fragment", columnDefinition = "TEXT")
    private String quotedFragment;

    @Column(name = "app_route", length = 500)
    private String appRoute;

    @Column(name = "permission_signature", length = 500)
    private String permissionSignature;

    @Column(name = "access_validation_result", length = 100)
    private String accessValidationResult;

    @Column(name = "access_validated_at")
    private Instant accessValidatedAt;
}
