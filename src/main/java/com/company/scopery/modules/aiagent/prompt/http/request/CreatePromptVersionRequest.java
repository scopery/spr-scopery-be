package com.company.scopery.modules.aiagent.prompt.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePromptVersionRequest(
        @NotNull UUID templateId,
        String title,
        @NotBlank String content,
        @NotBlank String contentFormat,
        String variableSchema,
        String changeNote
) {}
