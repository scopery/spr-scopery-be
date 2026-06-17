package com.company.scopery.modules.notification.emailtemplate.application.command;

import java.util.UUID;

public record CreateEmailTemplateCommand(
        String code,
        String name,
        String description,
        String scope,
        UUID workspaceId,
        UUID eventDefinitionId
) {}
