package com.company.scopery.modules.configuration.layout.domain.model;
import java.util.*; import java.util.UUID;
public interface LayoutDefinitionRepository {
    LayoutDefinition save(LayoutDefinition l);
    Optional<LayoutDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<LayoutDefinition> findByWorkspaceId(UUID workspaceId);
}
