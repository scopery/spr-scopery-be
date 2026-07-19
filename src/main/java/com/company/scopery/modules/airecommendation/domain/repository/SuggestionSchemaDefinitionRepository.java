package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.SuggestionSchemaDefinition;

import java.util.Optional;
import java.util.UUID;

public interface SuggestionSchemaDefinitionRepository {
    SuggestionSchemaDefinition save(SuggestionSchemaDefinition schema);
    Optional<SuggestionSchemaDefinition> findById(UUID id);
    Optional<SuggestionSchemaDefinition> findByCodeAndVersion(String code, int version);
    boolean existsByCodeAndVersion(String code, int version);
}
