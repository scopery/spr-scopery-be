package com.company.scopery.modules.knowledge.graph.application.response;

import java.util.UUID;

public record GraphNodeResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String nodeType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        String nodeStatus
) {}
