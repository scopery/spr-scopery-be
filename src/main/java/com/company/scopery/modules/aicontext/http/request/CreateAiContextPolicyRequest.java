package com.company.scopery.modules.aicontext.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateAiContextPolicyRequest(
        @NotNull UUID workspaceId,
        @NotBlank String policyCode,
        String label,
        @Positive int maxTokens,
        boolean includeRelated
) {}
