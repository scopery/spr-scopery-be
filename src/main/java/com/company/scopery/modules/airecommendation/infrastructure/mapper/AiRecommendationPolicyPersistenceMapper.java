package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.PolicyStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.AiRecommendationPolicyJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class AiRecommendationPolicyPersistenceMapper {

    public AiRecommendationPolicy toDomain(AiRecommendationPolicyJpaEntity entity) {
        return new AiRecommendationPolicy(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                PolicyStatus.valueOf(entity.getStatus()),
                entity.getScopeType(),
                parseList(entity.getTriggerModes()),
                parseList(entity.getPackCodes()),
                entity.isLlmEnrichmentEnabled(),
                entity.getMinConfidence(),
                entity.getDefaultSeverity(),
                entity.getDefaultCooldownMinutes(),
                entity.getMaxSuggestionsPerRun(),
                entity.isPublishToInbox(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersion()
        );
    }

    public AiRecommendationPolicyJpaEntity toJpaEntity(AiRecommendationPolicy domain) {
        AiRecommendationPolicyJpaEntity entity = new AiRecommendationPolicyJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setScopeType(domain.scopeType());
        entity.setTriggerModes(joinList(domain.triggerModes()));
        entity.setPackCodes(joinList(domain.packCodes()));
        entity.setLlmEnrichmentEnabled(domain.llmEnrichmentEnabled());
        entity.setMinConfidence(domain.minConfidence());
        entity.setDefaultSeverity(domain.defaultSeverity());
        entity.setDefaultCooldownMinutes(domain.defaultCooldownMinutes());
        entity.setMaxSuggestionsPerRun(domain.maxSuggestionsPerRun());
        entity.setPublishToInbox(domain.publishToInbox());
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
