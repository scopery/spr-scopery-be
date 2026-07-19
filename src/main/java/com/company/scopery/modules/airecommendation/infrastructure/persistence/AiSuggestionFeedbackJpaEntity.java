package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.FEEDBACK,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_feedback_suggestion_actor",
                columnNames = {"suggestion_id", "actor_id"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_feedback_suggestion_id", columnList = "suggestion_id"),
                @Index(name = "idx_ai_recommendation_feedback_actor_id",      columnList = "actor_id")
        }
)
public class AiSuggestionFeedbackJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "helpful")
    private Boolean helpful;

    @Column(name = "reason_code", length = 100)
    private String reasonCode;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "observed_outcome", columnDefinition = "TEXT")
    private String observedOutcome;
}
