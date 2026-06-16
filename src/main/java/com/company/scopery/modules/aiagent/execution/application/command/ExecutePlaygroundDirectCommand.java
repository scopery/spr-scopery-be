package com.company.scopery.modules.aiagent.execution.application.command;

import java.util.Map;
import java.util.UUID;

public record ExecutePlaygroundDirectCommand(
        String requestId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        Map<String, String> inputVariables
) {}
