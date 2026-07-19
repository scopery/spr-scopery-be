package com.company.scopery.modules.aiagent.tool.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ExecuteAiToolCommand(
        UUID toolId,
        UUID agentId,
        String inputSummary,
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        List<String> aclTokens,
        Map<String, Object> arguments
) {}
