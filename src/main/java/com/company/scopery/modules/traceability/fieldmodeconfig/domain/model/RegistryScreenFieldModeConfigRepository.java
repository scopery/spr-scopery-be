package com.company.scopery.modules.traceability.fieldmodeconfig.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryScreenFieldModeConfigRepository {
    RegistryScreenFieldModeConfig save(RegistryScreenFieldModeConfig entity);
    List<RegistryScreenFieldModeConfig> saveAll(List<RegistryScreenFieldModeConfig> entities);
    Optional<RegistryScreenFieldModeConfig> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryScreenFieldModeConfig> findByFieldId(UUID fieldId);
    List<RegistryScreenFieldModeConfig> findByFieldIdIn(Collection<UUID> fieldIds);
    int deleteByIdIn(List<UUID> ids);
}
