package com.company.scopery.modules.aiagent.tool.http.request;

import jakarta.validation.constraints.NotBlank;

public record AddAiToolPermissionRequest(
        @NotBlank String permissionCode,
        String description
) {}
