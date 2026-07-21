package com.company.scopery.modules.aiaction.tool.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiActionSchemaDefinitionRepository {

    AiActionSchemaDefinition save(AiActionSchemaDefinition schema);

    Optional<AiActionSchemaDefinition> findBySchemaCodeAndVersion(String schemaCode, int schemaVersion);

    boolean existsBySchemaCodeAndVersion(String schemaCode, int schemaVersion);
}
