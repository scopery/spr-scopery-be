package com.company.scopery.modules.traceability.componentapi.domain.model;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryComponentApiRepository {
    RegistryComponentApi save(RegistryComponentApi entity);
    Optional<RegistryComponentApi> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    boolean existsByComponentIdAndApiIdAndRole(UUID componentId, UUID apiId, ComponentApiRole role);
    List<RegistryComponentApi> findByComponentId(UUID componentId);
    void delete(UUID id);
}
