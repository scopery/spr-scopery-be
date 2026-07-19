package com.company.scopery.modules.knowledge.graph.domain.model;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphEdgeStatus;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphEdgeType;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeGraphEdge(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID fromNodeId,
        UUID toNodeId,
        GraphEdgeType edgeType,
        UUID sourceRefId,
        GraphEdgeStatus edgeStatus,
        Instant createdAt,
        Instant updatedAt,
        long version
) {}
