package com.company.scopery.modules.traceability.componentfield.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryComponentFieldRepository {
    Optional<RegistryComponentField> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    boolean existsByComponentIdAndFieldKey(UUID componentId, String fieldKey);
    List<RegistryComponentField> findByComponentIdOrderByDisplayOrderAsc(UUID componentId);
    RegistryComponentField save(RegistryComponentField field);
    void delete(UUID id);
}
