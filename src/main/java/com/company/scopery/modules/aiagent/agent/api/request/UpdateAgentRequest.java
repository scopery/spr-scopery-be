package com.company.scopery.modules.aiagent.agent.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateAgentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat
) {}