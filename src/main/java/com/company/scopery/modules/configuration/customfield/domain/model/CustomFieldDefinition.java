package com.company.scopery.modules.configuration.customfield.domain.model;
import com.company.scopery.modules.configuration.customfield.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record CustomFieldDefinition(UUID id, UUID workspaceId, String objectTypeCode, String fieldKey, String label, CustomFieldType fieldType,
        boolean required, boolean sensitive, boolean clientVisible, boolean searchable, boolean reportable, boolean exportable,
        String defaultValueJson, CustomFieldStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static CustomFieldDefinition create(UUID workspaceId, String objectType, String fieldKey, String label, CustomFieldType type,
                                               boolean required, boolean sensitive, boolean clientVisible) {
        Instant now = Instant.now();
        return new CustomFieldDefinition(UUID.randomUUID(), workspaceId, objectType, fieldKey, label, type, required, sensitive, clientVisible,
                false, false, false, null, CustomFieldStatus.ACTIVE, 0, now, now);
    }
    public CustomFieldDefinition archive() {
        return new CustomFieldDefinition(id, workspaceId, objectTypeCode, fieldKey, label, fieldType, required, sensitive, clientVisible,
                searchable, reportable, exportable, defaultValueJson, CustomFieldStatus.ARCHIVED, version, createdAt, Instant.now());
    }
}
