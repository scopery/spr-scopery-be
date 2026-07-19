package com.company.scopery.modules.aiagent.tool.application.query;

import java.util.UUID;

public record SearchAiToolExecutionQuery(
        UUID toolId,
        UUID agentId,
        String status,
        int page,
        int size
) {}
