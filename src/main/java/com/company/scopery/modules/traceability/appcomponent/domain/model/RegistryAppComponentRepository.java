package com.company.scopery.modules.traceability.appcomponent.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RegistryAppComponentRepository {
    RegistryAppComponent save(RegistryAppComponent e);
    Optional<RegistryAppComponent> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryAppComponent> findByApplicationId(UUID applicationId);
}
