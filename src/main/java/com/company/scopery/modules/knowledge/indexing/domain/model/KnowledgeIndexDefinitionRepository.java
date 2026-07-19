package com.company.scopery.modules.knowledge.indexing.domain.model;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexDefinitionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeIndexDefinitionRepository {
    KnowledgeIndexDefinition save(KnowledgeIndexDefinition definition);
    Optional<KnowledgeIndexDefinition> findById(UUID id);
    Optional<KnowledgeIndexDefinition> findByCode(String code);
    List<KnowledgeIndexDefinition> findByEnvironmentAndStatus(String environment, IndexDefinitionStatus status);
}
