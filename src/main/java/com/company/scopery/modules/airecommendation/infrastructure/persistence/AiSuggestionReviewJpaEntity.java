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

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.REVIEW,
        indexes = {
                @Index(name = "idx_ai_recommendation_review_suggestion_id", columnList = "suggestion_id"),
                @Index(name = "idx_ai_recommendation_review_actor_id",      columnList = "actor_id")
        }
)
public class AiSuggestionReviewJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "decision", nullable = false, length = 100)
    private String decision;

    @Column(name = "from_status", nullable = false, length = 100)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 100)
    private String toStatus;

    @Column(name = "expected_suggestion_version", nullable = false)
    private long expectedSuggestionVersion;

    @Column(name = "reason_code", length = 100)
    private String reasonCode;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "edited_items_json", columnDefinition = "TEXT")
    private String editedItemsJson;

    @Column(name = "trace_id", length = 255)
    private String traceId;
}
