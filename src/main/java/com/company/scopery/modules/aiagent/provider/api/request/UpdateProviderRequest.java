package com.company.scopery.modules.aiagent.provider.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProviderRequest(
        @NotBlank(message = "Provider name is required")
        String name,

        @NotBlank(message = "Provider type is required")
        String type,

        String apiBaseUrl,
        String description
) {}
