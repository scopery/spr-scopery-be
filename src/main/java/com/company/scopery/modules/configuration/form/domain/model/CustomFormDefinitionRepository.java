package com.company.scopery.modules.configuration.form.domain.model;
import java.util.*; import java.util.UUID;
public interface CustomFormDefinitionRepository {
    CustomFormDefinition save(CustomFormDefinition f);
    Optional<CustomFormDefinition> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<CustomFormDefinition> findByWorkspaceId(UUID workspaceId);
    boolean existsByCode(UUID workspaceId, String formCode);
}
