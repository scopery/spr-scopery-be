package com.company.scopery.modules.aiagent.aimodel.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAiModelRequest(
        @NotBlank(message = "AI model name is required")
        String name,

        @NotBlank(message = "Provider model ID is required")
        String providerModelId,

        @NotBlank(message = "AI model type is required")
        String type,

        String description
) {}