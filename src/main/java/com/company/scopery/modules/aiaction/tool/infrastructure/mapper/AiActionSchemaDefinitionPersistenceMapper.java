package com.company.scopery.modules.aiaction.tool.infrastructure.mapper;

import com.company.scopery.modules.aiaction.tool.domain.model.AiActionSchemaDefinition;
import com.company.scopery.modules.aiaction.tool.infrastructure.persistence.AiActionSchemaDefinitionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiActionSchemaDefinitionPersistenceMapper {

    public AiActionSchemaDefinitionJpaEntity toJpaEntity(AiActionSchemaDefinition domain) {
        AiActionSchemaDefinitionJpaEntity entity = new AiActionSchemaDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setSchemaCode(domain.schemaCode());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setSchemaJson(domain.schemaJson());
        entity.setStatus(domain.status());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public AiActionSchemaDefinition toDomain(AiActionSchemaDefinitionJpaEntity entity) {
        return AiActionSchemaDefinition.reconstitute(
                entity.getId(),
                entity.getSchemaCode(),
                entity.getSchemaVersion(),
                entity.getSchemaJson(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
