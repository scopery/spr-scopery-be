package com.company.scopery.modules.project.template.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProjectTemplateRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotBlank String scope,
        UUID organizationId,
        UUID workspaceId,
        String category,
        String visibility,
        Boolean builtIn
) {}
