package com.company.scopery.modules.notification.emailtemplate.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEmailTemplateRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotBlank String scope,
        UUID workspaceId,
        @NotNull UUID eventDefinitionId
) {}
