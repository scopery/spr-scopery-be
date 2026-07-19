package com.company.scopery.modules.knowledge.retrieval.application.response;

import java.util.List;
import java.util.UUID;

public record RetrievalResponse(
        UUID workspaceId,
        UUID projectId,
        List<RetrievalResultItem> results,
        int durationMs
) {
    public static RetrievalResponse empty(UUID workspaceId, UUID projectId) {
        return new RetrievalResponse(workspaceId, projectId, List.of(), 0);
    }
}
