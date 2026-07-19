package com.company.scopery.modules.aiassistant.guide.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ExplainPageRequest(
        @NotNull UUID workspaceId,
        UUID projectId,
        @NotBlank String pageCode,
        String locale
) {}
