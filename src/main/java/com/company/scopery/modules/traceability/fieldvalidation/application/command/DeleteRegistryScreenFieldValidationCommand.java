package com.company.scopery.modules.traceability.fieldvalidation.application.command;

import java.util.UUID;

public record DeleteRegistryScreenFieldValidationCommand(
        UUID workspaceId,
        UUID screenId,
        UUID fieldId,
        UUID validationId) {}
