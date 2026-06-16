package com.company.scopery.modules.aiagent.agent.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAgentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[A-Za-z0-9_]+$",
                message = "Code must contain only letters, numbers, and underscores")
        String code,
        @NotBlank String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat
) {}