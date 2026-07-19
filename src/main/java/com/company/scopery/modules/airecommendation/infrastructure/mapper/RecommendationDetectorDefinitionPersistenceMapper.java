package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.DetectorExecutionMethod;
import com.company.scopery.modules.airecommendation.domain.enums.DetectorStatus;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionSeverity;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.RecommendationDetectorDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class RecommendationDetectorDefinitionPersistenceMapper {

    public RecommendationDetectorDefinition toDomain(RecommendationDetectorDefinitionJpaEntity entity) {
        return new RecommendationDetectorDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getVersion(),
                entity.getPackCode(),
                entity.getSuggestionType(),
                entity.getSchemaCode(),
                entity.getSchemaVersion(),
                DetectorExecutionMethod.valueOf(entity.getExecutionMethod()),
                entity.getDefaultConfidence(),
                entity.getDefaultSeverity() != null
                        ? SuggestionSeverity.valueOf(entity.getDefaultSeverity()) : null,
                entity.getDefaultExpiryMinutes(),
                entity.getDefaultCooldownMinutes(),
                entity.isNonSuppressible(),
                DetectorStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersionLock()
        );
    }

    public RecommendationDetectorDefinitionJpaEntity toJpaEntity(RecommendationDetectorDefinition domain) {
        RecommendationDetectorDefinitionJpaEntity entity = new RecommendationDetectorDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setVersion(domain.version());
        entity.setPackCode(domain.packCode());
        entity.setSuggestionType(domain.suggestionType());
        entity.setSchemaCode(domain.schemaCode());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setExecutionMethod(domain.executionMethod().name());
        entity.setDefaultConfidence(domain.defaultConfidence());
        entity.setDefaultSeverity(domain.defaultSeverity() != null ? domain.defaultSeverity().name() : null);
        entity.setDefaultExpiryMinutes(domain.defaultExpiryMinutes());
        entity.setDefaultCooldownMinutes(domain.defaultCooldownMinutes());
        entity.setNonSuppressible(domain.nonSuppressible());
        entity.setStatus(domain.status().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }
}
