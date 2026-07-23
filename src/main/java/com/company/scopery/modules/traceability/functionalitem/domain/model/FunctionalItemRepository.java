package com.company.scopery.modules.traceability.functionalitem.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionalItemRepository {
    FunctionalItem save(FunctionalItem item);
    Optional<FunctionalItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<FunctionalItem> findByProjectId(UUID projectId);
    List<FunctionalItem> findByProjectIdAndModuleId(UUID projectId, UUID moduleId);
    List<FunctionalItem> findByModuleIdIn(Collection<UUID> moduleIds);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    void delete(UUID id, UUID projectId);
}
