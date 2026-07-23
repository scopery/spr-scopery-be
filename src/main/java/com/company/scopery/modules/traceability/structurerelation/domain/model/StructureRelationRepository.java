package com.company.scopery.modules.traceability.structurerelation.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StructureRelationRepository {
    StructureRelation save(StructureRelation rel);
    Optional<StructureRelation> findByIdAndApplicationId(UUID id, UUID applicationId);
    List<StructureRelation> findByApplicationId(UUID applicationId);
    List<StructureRelation> findByApplicationIdAndFromNode(UUID applicationId, String nodeType, UUID nodeId);
    List<StructureRelation> findByApplicationIdAndToNode(UUID applicationId, String nodeType, UUID nodeId);
    boolean existsByApplicationIdAndNodes(UUID applicationId, String fromNodeType, UUID fromNodeId, String toNodeType, UUID toNodeId);
    void delete(UUID id, UUID applicationId);
}
