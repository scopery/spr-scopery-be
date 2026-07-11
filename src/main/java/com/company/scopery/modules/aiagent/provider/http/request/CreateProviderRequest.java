package com.company.scopery.modules.aiagent.provider.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProviderRequest(
        @NotBlank(message = "Provider name is required")
        String name,

        @NotBlank(message = "Provider code is required")
        String code,

        @NotBlank(message = "Provider type is required")
        String type,

        String apiBaseUrl,
        String description
) {}
