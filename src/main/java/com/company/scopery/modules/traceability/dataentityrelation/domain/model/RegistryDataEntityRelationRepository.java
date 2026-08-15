package com.company.scopery.modules.traceability.dataentityrelation.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistryDataEntityRelationRepository {
    RegistryDataEntityRelation save(RegistryDataEntityRelation relation);
    Optional<RegistryDataEntityRelation> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<RegistryDataEntityRelation> findByEntityId(UUID entityId);
    boolean existsBySourceEntityIdAndTargetEntityIdAndRelationType(UUID sourceEntityId, UUID targetEntityId, String relationType);
    void deleteById(UUID id);
}
