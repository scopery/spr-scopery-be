package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiGuideDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiGuideDefinitionPersistenceMapper {

    public AiGuideDefinitionJpaEntity toJpaEntity(AiGuideDefinition domain) {
        AiGuideDefinitionJpaEntity entity = new AiGuideDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setPageCode(domain.pageCode());
        entity.setFieldCode(domain.fieldCode());
        entity.setActionCode(domain.actionCode());
        entity.setLocale(domain.locale());
        entity.setTitle(domain.title());
        entity.setBodyMarkdown(domain.bodyMarkdown());
        entity.setMetadataVersion(domain.metadataVersion());
        entity.setSourceKind(domain.sourceKind());
        entity.setStatus(domain.status());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }

    public AiGuideDefinition toDomain(AiGuideDefinitionJpaEntity entity) {
        return AiGuideDefinition.reconstitute(
                entity.getId(),
                entity.getCode(),
                entity.getPageCode(),
                entity.getFieldCode(),
                entity.getActionCode(),
                entity.getLocale(),
                entity.getTitle(),
                entity.getBodyMarkdown(),
                entity.getMetadataVersion(),
                entity.getSourceKind(),
                entity.getStatus(),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion()
        );
    }

    private static UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
