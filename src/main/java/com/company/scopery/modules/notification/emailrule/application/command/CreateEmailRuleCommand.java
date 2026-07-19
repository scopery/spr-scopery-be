package com.company.scopery.modules.notification.emailrule.application.command;

import java.util.UUID;

public record CreateEmailRuleCommand(
        String code,
        String name,
        String description,
        String scope,
        UUID workspaceId,
        UUID eventDefinitionId,
        UUID templateId,
        String recipientStrategy,
        String recipientConfigJson,
        int priority,
        boolean mandatory,
        boolean allowSensitiveVariables
) {}
