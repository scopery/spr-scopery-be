package com.company.scopery.modules.aiagent.tool.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAiToolRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotBlank String category,
        @NotBlank String mutationType,
        @NotNull Boolean requiresHumanApproval
) {}
