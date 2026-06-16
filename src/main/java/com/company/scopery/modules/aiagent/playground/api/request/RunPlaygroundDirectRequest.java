package com.company.scopery.modules.aiagent.playground.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record RunPlaygroundDirectRequest(
        @Size(max = 128) String requestId,
        @NotNull UUID agentId,
        @NotNull UUID promptVersionId,
        @NotNull UUID modelDeploymentId,
        Map<String, String> inputVariables
) {}
