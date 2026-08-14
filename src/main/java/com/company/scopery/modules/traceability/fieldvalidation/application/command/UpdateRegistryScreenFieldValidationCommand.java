package com.company.scopery.modules.traceability.fieldvalidation.application.command;

import java.util.UUID;

public record UpdateRegistryScreenFieldValidationCommand(
        UUID workspaceId,
        UUID screenId,
        UUID fieldId,
        UUID validationId,
        UUID modeId,
        String ruleParamJson,
        String conditionJson,
        String errorMessage,
        String remark,
        int displayOrder) {}
