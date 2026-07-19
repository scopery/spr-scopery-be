package com.company.scopery.modules.knowledge.graph.application.response;

import java.util.UUID;

public record GraphEdgeResponse(
        UUID id,
        UUID fromNodeId,
        UUID toNodeId,
        String edgeType,
        String edgeStatus
) {}
