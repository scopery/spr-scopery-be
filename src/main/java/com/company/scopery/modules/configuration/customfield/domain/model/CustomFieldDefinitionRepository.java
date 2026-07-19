package com.company.scopery.modules.configuration.customfield.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface CustomFieldDefinitionRepository {
    CustomFieldDefinition save(CustomFieldDefinition d);
    Optional<CustomFieldDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    boolean existsByKey(UUID workspaceId, String objectTypeCode, String fieldKey);
    List<CustomFieldDefinition> findByWorkspaceId(UUID workspaceId);
}
