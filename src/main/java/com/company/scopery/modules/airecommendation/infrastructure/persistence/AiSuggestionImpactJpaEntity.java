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

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.IMPACT,
        indexes = {
                @Index(name = "idx_ai_recommendation_impact_suggestion_id", columnList = "suggestion_id"),
                @Index(name = "idx_ai_recommendation_impact_dimension",     columnList = "suggestion_id,dimension")
        }
)
public class AiSuggestionImpactJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "dimension", nullable = false, length = 100)
    private String dimension;

    @Column(name = "direction", nullable = false, length = 100)
    private String direction;

    @Column(name = "assessment_type", nullable = false, length = 100)
    private String assessmentType;

    @Column(name = "numeric_value", precision = 19, scale = 4)
    private BigDecimal numericValue;

    @Column(name = "unit_code", length = 100)
    private String unitCode;

    @Column(name = "qualitative_magnitude", length = 100)
    private String qualitativeMagnitude;

    @Column(name = "source_method", length = 100)
    private String sourceMethod;

    @Column(name = "calculation_method_code", length = 100)
    private String calculationMethodCode;

    @Column(name = "assumptions", columnDefinition = "TEXT")
    private String assumptions;

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "source_ref_id")
    private UUID sourceRefId;
}
