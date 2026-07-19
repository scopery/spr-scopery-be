package com.company.scopery.modules.aiagent.tool.application.port;

import java.util.List;
import java.util.UUID;

public record AiToolExecutionContext(
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        List<String> aclTokens,
        String classificationClearance,
        String traceId,
        String correlationId
) {}
