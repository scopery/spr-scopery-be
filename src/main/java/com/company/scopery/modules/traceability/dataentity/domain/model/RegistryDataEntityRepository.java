package com.company.scopery.modules.traceability.dataentity.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryDataEntityRepository {
    RegistryDataEntity save(RegistryDataEntity entity);
    Optional<RegistryDataEntity> findById(UUID id);
    List<RegistryDataEntity> findByIdIn(Collection<UUID> ids);
    Optional<RegistryDataEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntity> findByApplicationId(UUID applicationId);
    List<RegistryDataEntity> findByApplicationIdAndModuleId(UUID applicationId, UUID moduleId);
    void delete(UUID id, UUID workspaceId);
}
