package com.company.scopery.modules.knowledge.graph.domain.model;

import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeStatus;
import com.company.scopery.modules.knowledge.graph.domain.enums.GraphNodeType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record KnowledgeGraphNode(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        GraphNodeType nodeType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        String permissionSignature,
        List<String> aclTokens,
        GraphNodeStatus nodeStatus,
        Instant createdAt,
        Instant updatedAt,
        long version
) {}
