package com.company.scopery.modules.aiagent.aimodel.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAiModelRequest(
        @NotNull(message = "Provider ID is required")
        UUID providerId,

        @NotBlank(message = "AI model name is required")
        String name,

        @NotBlank(message = "AI model code is required")
        String code,

        @NotBlank(message = "Provider model ID is required")
        String providerModelId,

        @NotBlank(message = "AI model type is required")
        String type,

        String description
) {}
