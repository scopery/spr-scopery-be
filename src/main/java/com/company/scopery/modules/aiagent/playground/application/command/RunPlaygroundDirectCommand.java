package com.company.scopery.modules.aiagent.playground.application.command;

import java.util.Map;
import java.util.UUID;

public record RunPlaygroundDirectCommand(
        String requestId,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        Map<String, String> inputVariables
) {}
