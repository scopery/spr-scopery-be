package com.company.scopery.modules.knowledge.indexing.infrastructure.mapper;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexDefinitionStatus;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexDefinition;
import com.company.scopery.modules.knowledge.indexing.infrastructure.persistence.KnowledgeIndexDefinitionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class KnowledgeIndexDefinitionPersistenceMapper {

    public KnowledgeIndexDefinition toDomain(KnowledgeIndexDefinitionJpaEntity entity) {
        return new KnowledgeIndexDefinition(
                entity.getId(),
                entity.getCode(),
                entity.getEnvironment(),
                entity.getIndexFamily(),
                entity.getSchemaVersion(),
                entity.getEmbeddingProfileId(),
                entity.getChunkStrategyVersion(),
                entity.getReadAlias(),
                entity.getWriteAlias(),
                entity.getActiveGeneration(),
                entity.getActiveConcreteIndex(),
                entity.getMappingHash(),
                IndexDefinitionStatus.valueOf(entity.getDefinitionStatus()),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion() != null ? entity.getVersion() : 0L
        );
    }

    private UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public KnowledgeIndexDefinitionJpaEntity toJpaEntity(KnowledgeIndexDefinition domain) {
        KnowledgeIndexDefinitionJpaEntity entity = new KnowledgeIndexDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setEnvironment(domain.environment());
        entity.setIndexFamily(domain.indexFamily());
        entity.setSchemaVersion(domain.schemaVersion());
        entity.setEmbeddingProfileId(domain.embeddingProfileId());
        entity.setChunkStrategyVersion(domain.chunkStrategyVersion());
        entity.setReadAlias(domain.readAlias());
        entity.setWriteAlias(domain.writeAlias());
        entity.setActiveGeneration(domain.activeGeneration());
        entity.setActiveConcreteIndex(domain.activeConcreteIndex());
        entity.setMappingHash(domain.mappingHash());
        entity.setDefinitionStatus(domain.definitionStatus().name());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            if (domain.version() > 0) {
                entity.setVersion(domain.version());
            }
        }
        return entity;
    }
}
