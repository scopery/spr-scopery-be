package com.company.scopery.modules.configuration.validation.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFieldValidationRule(UUID id, UUID workspaceId, UUID customFieldDefinitionId, String ruleType, String ruleConfigJson,
        String status, int version, Instant createdAt, Instant updatedAt) {
    public static CustomFieldValidationRule create(UUID workspaceId, UUID fieldId, String ruleType, String config) {
        Instant now = Instant.now();
        return new CustomFieldValidationRule(UUID.randomUUID(), workspaceId, fieldId, ruleType, config, "ACTIVE", 0, now, now);
    }
}
