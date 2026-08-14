package com.company.scopery.modules.traceability.fieldvalidation.application.command;

import java.util.UUID;

public record CreateRegistryScreenFieldValidationCommand(
        UUID workspaceId,
        UUID screenId,
        UUID fieldId,
        UUID ruleTypeId,
        UUID modeId,
        String ruleParamJson,
        String conditionJson,
        String errorMessage,
        String remark,
        int displayOrder) {}
