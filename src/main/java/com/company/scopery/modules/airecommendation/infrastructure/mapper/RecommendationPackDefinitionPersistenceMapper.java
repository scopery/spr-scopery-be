package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.PackStatus;
import com.company.scopery.modules.airecommendation.domain.model.RecommendationPackDefinition;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.RecommendationPackDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RecommendationPackDefinitionPersistenceMapper {

    public RecommendationPackDefinition toDomain(RecommendationPackDefinitionJpaEntity entity) {
        return new RecommendationPackDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getVersion(),
                entity.getName(),
                entity.getDescription(),
                parseList(entity.getDetectorCodes()),
                parseList(entity.getAllowedTriggerModes()),
                entity.isLlmEnrichmentEnabled(),
                entity.getDefaultCooldownMinutes(),
                entity.getDefaultExpiryMinutes(),
                entity.getMaxSuggestionsPerRun(),
                PackStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersionLock()
        );
    }

    public RecommendationPackDefinitionJpaEntity toJpaEntity(RecommendationPackDefinition domain) {
        RecommendationPackDefinitionJpaEntity entity = new RecommendationPackDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setVersion(domain.version());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setDetectorCodes(joinList(domain.detectorCodes()));
        entity.setAllowedTriggerModes(joinList(domain.allowedTriggerModes()));
        entity.setLlmEnrichmentEnabled(domain.llmEnrichmentEnabled());
        entity.setDefaultCooldownMinutes(domain.defaultCooldownMinutes());
        entity.setDefaultExpiryMinutes(domain.defaultExpiryMinutes());
        entity.setMaxSuggestionsPerRun(domain.maxSuggestionsPerRun());
        entity.setStatus(domain.status().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt().toInstant());
        }
        return entity;
    }

    private List<String> parseList(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return String.join(",", list);
    }
}
