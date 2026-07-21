package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionSchemaDefinitionJpaRepository extends JpaRepository<AiActionSchemaDefinitionJpaEntity, UUID> {

    Optional<AiActionSchemaDefinitionJpaEntity> findBySchemaCodeAndSchemaVersion(String schemaCode, int schemaVersion);

    Optional<AiActionSchemaDefinitionJpaEntity> findBySchemaCodeAndStatus(String schemaCode, String status);

    boolean existsBySchemaCodeAndSchemaVersion(String schemaCode, int schemaVersion);
}
