package com.company.scopery.modules.airecommendation.infrastructure.mapper;

import com.company.scopery.modules.airecommendation.domain.enums.ActionKind;
import com.company.scopery.modules.airecommendation.domain.enums.NbaStatus;
import com.company.scopery.modules.airecommendation.domain.model.NextBestActionDefinition;
import com.company.scopery.modules.airecommendation.infrastructure.persistence.NextBestActionDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class NextBestActionDefinitionPersistenceMapper {

    public NextBestActionDefinition toDomain(NextBestActionDefinitionJpaEntity entity) {
        return new NextBestActionDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getVersion(),
                entity.getLabel(),
                entity.getDescription(),
                ActionKind.valueOf(entity.getActionKind()),
                parseList(entity.getApplicableSuggestionTypes()),
                entity.getRequiredAuthorityCode(),
                entity.getRequiredTargetCapabilityCode(),
                entity.getPhase44ToolCode(),
                entity.getPhase44ToolVersion(),
                entity.getRiskLevel(),
                NbaStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atOffset(ZoneOffset.UTC) : null,
                entity.getVersionLock()
        );
    }

    public NextBestActionDefinitionJpaEntity toJpaEntity(NextBestActionDefinition domain) {
        NextBestActionDefinitionJpaEntity entity = new NextBestActionDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setVersion(domain.version());
        entity.setLabel(domain.label());
        entity.setDescription(domain.description());
        entity.setActionKind(domain.actionKind().name());
        entity.setApplicableSuggestionTypes(joinList(domain.applicableSuggestionTypes()));
        entity.setRequiredAuthorityCode(domain.requiredAuthorityCode());
        entity.setRequiredTargetCapabilityCode(domain.requiredTargetCapabilityCode());
        entity.setPhase44ToolCode(domain.phase44ToolCode());
        entity.setPhase44ToolVersion(domain.phase44ToolVersion());
        entity.setRiskLevel(domain.riskLevel());
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
