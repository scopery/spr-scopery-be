package com.company.scopery.modules.traceability.componentoption.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryComponentOptionRepository {
    RegistryComponentOption save(RegistryComponentOption entity);
    Optional<RegistryComponentOption> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryComponentOption> findByComponentId(UUID componentId);
    List<RegistryComponentOption> findByComponentIdIn(Collection<UUID> componentIds);
    void delete(UUID id, UUID workspaceId);
}
