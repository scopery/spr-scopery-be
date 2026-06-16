package com.company.scopery.modules.aiagent.prompt.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePromptVersionRequest(
        String title,
        @NotBlank String content,
        @NotBlank String contentFormat,
        String variableSchema,
        String changeNote
) {}