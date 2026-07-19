package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataKnowledgeGraphEdgeJpaRepository extends JpaRepository<KnowledgeGraphEdgeJpaEntity, UUID> {
    List<KnowledgeGraphEdgeJpaEntity> findByFromNodeId(UUID fromNodeId);
    List<KnowledgeGraphEdgeJpaEntity> findByToNodeId(UUID toNodeId);

    @Query("SELECT e FROM KnowledgeGraphEdgeJpaEntity e WHERE (e.fromNodeId = :nodeId OR e.toNodeId = :nodeId) AND e.edgeStatus = 'ACTIVE'")
    List<KnowledgeGraphEdgeJpaEntity> findActiveByNodeId(@Param("nodeId") UUID nodeId);
}
