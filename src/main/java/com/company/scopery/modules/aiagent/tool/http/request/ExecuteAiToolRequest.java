package com.company.scopery.modules.aiagent.tool.http.request;

import java.util.UUID;

public record ExecuteAiToolRequest(
        UUID agentId,
        String inputSummary
) {}
