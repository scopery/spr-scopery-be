package com.company.scopery.modules.traceability.appmodule.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RegistryAppModuleRepository {
    RegistryAppModule save(RegistryAppModule e);
    Optional<RegistryAppModule> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryAppModule> findByApplicationId(UUID applicationId);
}
