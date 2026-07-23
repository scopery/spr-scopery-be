package com.company.scopery.modules.traceability.dataentity.domain.model;
import java.util.*;
public interface RegistryDataEntityRepository {
    RegistryDataEntity save(RegistryDataEntity entity);
    Optional<RegistryDataEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntity> findByApplicationId(UUID applicationId);
    List<RegistryDataEntity> findByApplicationIdAndModuleId(UUID applicationId, UUID moduleId);
    void delete(UUID id, UUID workspaceId);
}
