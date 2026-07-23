package com.company.scopery.modules.traceability.nonfunctionalitem.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NonFunctionalItemRepository {

    NonFunctionalItem save(NonFunctionalItem item);

    Optional<NonFunctionalItem> findByIdAndProjectId(UUID id, UUID projectId);

    List<NonFunctionalItem> findByProjectId(UUID projectId);

    List<NonFunctionalItem> findByProjectIdAndIdIn(UUID projectId, Collection<UUID> ids);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    void delete(UUID id, UUID projectId);
}
