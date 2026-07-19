package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeIndexDefinitionJpaRepository extends JpaRepository<KnowledgeIndexDefinitionJpaEntity, UUID> {
    Optional<KnowledgeIndexDefinitionJpaEntity> findByCode(String code);
    List<KnowledgeIndexDefinitionJpaEntity> findByEnvironmentAndDefinitionStatus(String environment, String definitionStatus);
}
