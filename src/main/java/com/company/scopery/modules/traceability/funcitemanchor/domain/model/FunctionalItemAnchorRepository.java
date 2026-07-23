package com.company.scopery.modules.traceability.funcitemanchor.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionalItemAnchorRepository {
    FunctionalItemAnchor save(FunctionalItemAnchor anchor);
    Optional<FunctionalItemAnchor> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);
    List<FunctionalItemAnchor> findByFunctionalItemId(UUID functionalItemId);
    boolean existsByFunctionalItemIdAndNodeTypeAndNodeId(UUID functionalItemId, String nodeType, UUID nodeId);
    List<FunctionalItemAnchor> findByProjectId(UUID projectId);
    List<FunctionalItemAnchor> findByNodeTypeAndNodeIdAndProjectId(String nodeType, UUID nodeId, UUID projectId);
    void delete(UUID id, UUID functionalItemId);
}
