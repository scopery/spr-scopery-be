package com.company.scopery.modules.traceability.fieldvalidation.application.response;

import com.company.scopery.modules.traceability.fieldvalidation.domain.model.RegistryScreenFieldValidation;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenFieldValidationResponse(
        UUID id,
        UUID fieldId,
        UUID modeId,
        UUID ruleTypeId,
        UUID workspaceId,
        String ruleParamJson,
        String conditionJson,
        String errorMessage,
        String remark,
        int displayOrder,
        String status,
        Instant createdAt) {

    public static RegistryScreenFieldValidationResponse from(RegistryScreenFieldValidation v) {
        return new RegistryScreenFieldValidationResponse(
                v.id(),
                v.fieldId(),
                v.modeId(),
                v.ruleTypeId(),
                v.workspaceId(),
                v.ruleParamJson(),
                v.conditionJson(),
                v.errorMessage(),
                v.remark(),
                v.displayOrder(),
                v.status().name(),
                v.createdAt());
    }
}
