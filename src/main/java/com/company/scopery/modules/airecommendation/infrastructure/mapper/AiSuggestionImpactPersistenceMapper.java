package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.AssessmentType;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactDimension;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactDirection;
import com.company.scopery.modules.airecommendation.domain.enums.ImpactSourceMethod;
import com.company.scopery.modules.airecommendation.domain.enums.QualitativeMagnitude;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionImpact;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiSuggestionImpactJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class AiSuggestionImpactPersistenceMapper {

    public AiSuggestionImpact toDomain(AiSuggestionImpactJpaEntity entity) {
        return new AiSuggestionImpact(
                entity.getId(),
                entity.getSuggestionId(),
                ImpactDimension.valueOf(entity.getDimension()),
                ImpactDirection.valueOf(entity.getDirection()),
                AssessmentType.valueOf(entity.getAssessmentType()),
                entity.getNumericValue(),
                entity.getUnitCode(),
                entity.getQualitativeMagnitude() != null
                        ? QualitativeMagnitude.valueOf(entity.getQualitativeMagnitude()) : null,
                entity.getSourceMethod() != null ? ImpactSourceMethod.valueOf(entity.getSourceMethod()) : null,
                entity.getCalculationMethodCode(),
                entity.getAssumptions(),
                entity.getSourceType(),
                entity.getSourceRefId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null
        );
    }

    public AiSuggestionImpactJpaEntity toJpaEntity(AiSuggestionImpact domain) {
        AiSuggestionImpactJpaEntity entity = new AiSuggestionImpactJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setDimension(domain.dimension().name());
        entity.setDirection(domain.direction().name());
        entity.setAssessmentType(domain.assessmentType().name());
        entity.setNumericValue(domain.numericValue());
        entity.setUnitCode(domain.unitCode());
        entity.setQualitativeMagnitude(
                domain.qualitativeMagnitude() != null ? domain.qualitativeMagnitude().name() : null);
        entity.setSourceMethod(domain.sourceMethod() != null ? domain.sourceMethod().name() : null);
        entity.setCalculationMethodCode(domain.calculationMethodCode());
        entity.setAssumptions(domain.assumptions());
        entity.setSourceType(domain.sourceType());
        entity.setSourceRefId(domain.sourceRefId());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
