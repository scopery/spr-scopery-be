package com.company.scopery.modules.traceability.structurerelation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataStructureRelationJpaRepository extends JpaRepository<StructureRelationJpaEntity, UUID> {

    Optional<StructureRelationJpaEntity> findByIdAndApplicationId(UUID id, UUID applicationId);

    List<StructureRelationJpaEntity> findByApplicationIdOrderByCreatedAtAsc(UUID applicationId);

    List<StructureRelationJpaEntity> findByApplicationIdAndFromNodeTypeAndFromNodeIdOrderByCreatedAtAsc(
            UUID applicationId, String fromNodeType, UUID fromNodeId);

    List<StructureRelationJpaEntity> findByApplicationIdAndToNodeTypeAndToNodeIdOrderByCreatedAtAsc(
            UUID applicationId, String toNodeType, UUID toNodeId);

    boolean existsByApplicationIdAndFromNodeTypeAndFromNodeIdAndToNodeTypeAndToNodeId(
            UUID applicationId, String fromNodeType, UUID fromNodeId, String toNodeType, UUID toNodeId);

    void deleteByIdAndApplicationId(UUID id, UUID applicationId);
}
