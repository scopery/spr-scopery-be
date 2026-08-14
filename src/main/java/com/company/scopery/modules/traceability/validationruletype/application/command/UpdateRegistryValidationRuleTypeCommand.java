package com.company.scopery.modules.traceability.validationruletype.application.command;

import java.util.UUID;

public record UpdateRegistryValidationRuleTypeCommand(
        UUID workspaceId,
        UUID id,
        String name,
        String category,
        String paramSchemaJson,
        String defaultMessage,
        String description,
        int displayOrder
) {}
