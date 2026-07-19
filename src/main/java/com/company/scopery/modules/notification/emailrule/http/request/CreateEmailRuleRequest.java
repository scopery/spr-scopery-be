package com.company.scopery.modules.notification.emailrule.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEmailRuleRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotBlank String scope,
        UUID workspaceId,
        @NotNull UUID eventDefinitionId,
        @NotNull UUID templateId,
        @NotBlank String recipientStrategy,
        String recipientConfigJson,
        int priority,
        Boolean mandatory,
        Boolean allowSensitiveVariables
) {}
