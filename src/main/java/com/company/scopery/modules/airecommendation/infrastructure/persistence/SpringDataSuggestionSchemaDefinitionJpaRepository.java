package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSuggestionSchemaDefinitionJpaRepository
        extends JpaRepository<SuggestionSchemaDefinitionJpaEntity, UUID> {

    Optional<SuggestionSchemaDefinitionJpaEntity> findByCodeAndSchemaVersion(String code, int schemaVersion);

    boolean existsByCodeAndSchemaVersion(String code, int schemaVersion);
}
