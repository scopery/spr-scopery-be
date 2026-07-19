package com.company.scopery.modules.aiassistant.guide.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ExplainFieldRequest(
        @NotNull UUID workspaceId,
        UUID projectId,
        @NotBlank String pageCode,
        @NotBlank String fieldCode,
        String locale
) {}
