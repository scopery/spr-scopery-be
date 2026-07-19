package com.company.scopery.modules.knowledge.indexing.domain.model;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexDefinitionStatus;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeIndexDefinition(
        UUID id,
        String code,
        String environment,
        String indexFamily,
        String schemaVersion,
        UUID embeddingProfileId,
        String chunkStrategyVersion,
        String readAlias,
        String writeAlias,
        String activeGeneration,
        String activeConcreteIndex,
        String mappingHash,
        IndexDefinitionStatus definitionStatus,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy,
        long version
) {}
