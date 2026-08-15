package com.company.scopery.modules.traceability.componentfield.application.response;

import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import java.time.Instant;
import java.util.UUID;

public record RegistryComponentFieldResponse(
        UUID id, UUID componentId, UUID workspaceId,
        String fieldKey, String label, String fieldType,
        boolean required, Integer maxLength, String remark,
        int displayOrder, String status, Instant createdAt) {

    public static RegistryComponentFieldResponse from(RegistryComponentField f) {
        return new RegistryComponentFieldResponse(
                f.id(), f.componentId(), f.workspaceId(),
                f.fieldKey(), f.label(), f.fieldType(),
                f.required(), f.maxLength(), f.remark(),
                f.displayOrder(), f.status().name(), f.createdAt());
    }
}
