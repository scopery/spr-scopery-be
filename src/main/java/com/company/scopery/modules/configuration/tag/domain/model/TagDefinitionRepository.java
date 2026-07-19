package com.company.scopery.modules.configuration.tag.domain.model;
import java.util.*; import java.util.UUID;
public interface TagDefinitionRepository {
    TagDefinition save(TagDefinition t);
    Optional<TagDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<TagDefinition> findByWorkspaceId(UUID workspaceId);
    boolean existsByCode(UUID workspaceId, String code);
}
