package com.company.scopery.modules.aiagent.prompt.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreatePromptTemplateRequest(
        @NotNull UUID agentId,
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[A-Za-z0-9_]+$",
                message = "Code must contain only letters, numbers, and underscores")
        String code,
        String description
) {}
