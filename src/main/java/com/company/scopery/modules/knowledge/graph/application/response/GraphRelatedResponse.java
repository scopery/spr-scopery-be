package com.company.scopery.modules.knowledge.graph.application.response;

import java.util.List;
import java.util.UUID;

public record GraphRelatedResponse(
        UUID rootNodeId,
        List<GraphNodeResponse> nodes,
        List<GraphEdgeResponse> edges
) {}
