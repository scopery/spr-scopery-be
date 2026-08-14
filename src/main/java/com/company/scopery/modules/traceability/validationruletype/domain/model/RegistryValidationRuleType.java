package com.company.scopery.modules.traceability.validationruletype.domain.model;

import com.company.scopery.modules.traceability.validationruletype.domain.enums.ValidationRuleTypeStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryValidationRuleType(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String category,
        String paramSchemaJson,
        String defaultMessage,
        String description,
        boolean isSystem,
        ValidationRuleTypeStatus status,
        int displayOrder,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static RegistryValidationRuleType create(UUID workspaceId, String code, String name, String category,
                                                     String paramSchemaJson, String defaultMessage, String description,
                                                     int displayOrder) {
        return new RegistryValidationRuleType(UUID.randomUUID(), workspaceId, code, name, category,
                paramSchemaJson, defaultMessage, description, false,
                ValidationRuleTypeStatus.ACTIVE, displayOrder, 0, null, null);
    }

    public static RegistryValidationRuleType createSystem(UUID id, String code, String name, String category,
                                                           String paramSchemaJson, String defaultMessage,
                                                           int displayOrder) {
        return new RegistryValidationRuleType(id, null, code, name, category,
                paramSchemaJson, defaultMessage, null, true,
                ValidationRuleTypeStatus.ACTIVE, displayOrder, 0, null, null);
    }

    public RegistryValidationRuleType withUpdated(String name, String category, String paramSchemaJson,
                                                   String defaultMessage, String description, int displayOrder) {
        return new RegistryValidationRuleType(id, workspaceId, code, name, category,
                paramSchemaJson, defaultMessage, description, isSystem, status, displayOrder, version, createdAt, Instant.now());
    }
}
