package com.company.scopery.modules.traceability.dataentityfield.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryDataEntityFieldRepository {
    RegistryDataEntityField save(RegistryDataEntityField entity);
    Optional<RegistryDataEntityField> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntityField> findByEntityId(UUID entityId);
    List<RegistryDataEntityField> findByIdIn(Collection<UUID> ids);
    void delete(UUID id, UUID workspaceId);
}
