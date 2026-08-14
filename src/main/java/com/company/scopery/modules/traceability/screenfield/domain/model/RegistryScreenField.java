package com.company.scopery.modules.traceability.screenfield.domain.model;
import com.company.scopery.modules.traceability.screenfield.domain.enums.RegistryScreenFieldStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenField(
        UUID id, UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
        String label, String fieldType, String description, boolean required,
        int displayOrder,
        UUID componentId, UUID dataEntityFieldId, Integer maxLength, String remark,
        RegistryScreenFieldStatus status, int version,
        Instant createdAt, Instant updatedAt) {

    public static RegistryScreenField create(UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
                                             String label, String fieldType, String description,
                                             boolean required, int displayOrder,
                                             UUID componentId, UUID dataEntityFieldId, Integer maxLength, String remark) {
        return new RegistryScreenField(UUID.randomUUID(), screenId, sectionId, workspaceId, fieldKey, label, fieldType,
                description, required, displayOrder, componentId, dataEntityFieldId, maxLength, remark,
                RegistryScreenFieldStatus.ACTIVE, 0, null, null);
    }

    public RegistryScreenField withUpdated(String label, String fieldType, String description, boolean required,
                                           int displayOrder, UUID componentId, UUID dataEntityFieldId,
                                           Integer maxLength, String remark) {
        return new RegistryScreenField(id, screenId, sectionId, workspaceId, fieldKey, label, fieldType, description,
                required, displayOrder, componentId, dataEntityFieldId, maxLength, remark,
                status, version, createdAt, Instant.now());
    }
}
