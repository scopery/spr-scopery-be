package com.company.scopery.modules.knowledge.graph.domain.model;

import java.util.List;
import java.util.UUID;

public interface KnowledgeGraphEdgeRepository {
    KnowledgeGraphEdge save(KnowledgeGraphEdge edge);
    List<KnowledgeGraphEdge> findByFromNodeId(UUID fromNodeId);
    List<KnowledgeGraphEdge> findByToNodeId(UUID toNodeId);
    List<KnowledgeGraphEdge> findActiveByNodeId(UUID nodeId);
}
