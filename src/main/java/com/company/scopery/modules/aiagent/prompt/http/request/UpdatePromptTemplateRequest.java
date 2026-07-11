package com.company.scopery.modules.aiagent.prompt.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePromptTemplateRequest(
        @NotBlank @Size(max = 255) String name,
        String description
) {}
