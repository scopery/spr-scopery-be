package com.company.scopery.modules.traceability.dataentity.domain.model;
import java.util.*;
public interface RegistryDataEntityRepository {
    RegistryDataEntity save(RegistryDataEntity entity);
    Optional<RegistryDataEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntity> findByApplicationId(UUID applicationId);
}
