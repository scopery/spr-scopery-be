package com.company.scopery.modules.configuration.fieldoption.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFieldOption(UUID id, UUID customFieldDefinitionId, String optionCode, String label, int sortOrder,
        String status, int version, Instant createdAt, Instant updatedAt) {
    public static CustomFieldOption create(UUID fieldId, String code, String label, int sortOrder) {
        Instant now = Instant.now();
        return new CustomFieldOption(UUID.randomUUID(), fieldId, code, label, sortOrder, "ACTIVE", 0, now, now);
    }
    public CustomFieldOption archive() {
        return new CustomFieldOption(id, customFieldDefinitionId, optionCode, label, sortOrder, "ARCHIVED", version, createdAt, Instant.now());
    }
}
