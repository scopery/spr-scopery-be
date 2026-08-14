package com.company.scopery.modules.traceability.fieldvalidation.domain.model;

import com.company.scopery.modules.traceability.fieldvalidation.domain.enums.FieldValidationStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenFieldValidation(
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
        FieldValidationStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenFieldValidation create(UUID fieldId, UUID modeId, UUID ruleTypeId, UUID workspaceId,
                                                        String ruleParamJson, String conditionJson,
                                                        String errorMessage, String remark, int displayOrder) {
        return new RegistryScreenFieldValidation(UUID.randomUUID(), fieldId, modeId, ruleTypeId, workspaceId,
                ruleParamJson, conditionJson, errorMessage, remark, displayOrder,
                FieldValidationStatus.ACTIVE, 0, null, null);
    }

    public RegistryScreenFieldValidation withUpdated(UUID modeId, String ruleParamJson, String conditionJson,
                                                      String errorMessage, String remark, int displayOrder) {
        return new RegistryScreenFieldValidation(id, fieldId, modeId, ruleTypeId, workspaceId,
                ruleParamJson, conditionJson, errorMessage, remark, displayOrder,
                status, version, createdAt, Instant.now());
    }
}
