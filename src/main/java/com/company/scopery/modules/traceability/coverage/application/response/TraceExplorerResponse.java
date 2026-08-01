package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TraceExplorerResponse(
        String rootType,
        UUID rootId,
        TraceNode root,
        Instant generatedAt
) {
    public record TraceNode(
            UUID id,
            String objectType,
            String code,
            String name,
            String status,
            String latestResult,
            List<TraceNode> children
    ) {}
}
