package com.company.scopery.modules.traceability.componentfield.domain.model;

import com.company.scopery.modules.traceability.componentfield.domain.enums.RegistryComponentFieldStatus;
import java.time.Instant;
import java.util.UUID;

public record RegistryComponentField(
        UUID id, UUID componentId, UUID workspaceId,
        String fieldKey, String label, String fieldType,
        boolean required, Integer maxLength, String remark,
        int displayOrder,
        RegistryComponentFieldStatus status, int version,
        Instant createdAt, Instant updatedAt) {

    public static RegistryComponentField create(UUID componentId, UUID workspaceId,
            String fieldKey, String label, String fieldType,
            boolean required, Integer maxLength, String remark, int displayOrder) {
        return new RegistryComponentField(UUID.randomUUID(), componentId, workspaceId,
                fieldKey, label, fieldType, required, maxLength, remark, displayOrder,
                RegistryComponentFieldStatus.ACTIVE, 0, null, null);
    }

    public RegistryComponentField withUpdated(String label, String fieldType,
            boolean required, Integer maxLength, String remark, int displayOrder) {
        return new RegistryComponentField(id, componentId, workspaceId,
                fieldKey, label, fieldType, required, maxLength, remark, displayOrder,
                status, version, createdAt, Instant.now());
    }
}
