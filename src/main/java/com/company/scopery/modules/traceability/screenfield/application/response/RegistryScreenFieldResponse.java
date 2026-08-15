package com.company.scopery.modules.traceability.screenfield.application.response;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenFieldResponse(
        UUID id, UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
        String label, String fieldType, String description, boolean required,
        int displayOrder,
        UUID componentId, UUID dataEntityFieldId, Integer maxLength, String remark,
        UUID componentFieldId,
        String status, Instant createdAt) {

    public static RegistryScreenFieldResponse from(RegistryScreenField e) {
        return new RegistryScreenFieldResponse(e.id(), e.screenId(), e.sectionId(), e.workspaceId(), e.fieldKey(),
                e.label(), e.fieldType(), e.description(), e.required(), e.displayOrder(),
                e.componentId(), e.dataEntityFieldId(), e.maxLength(), e.remark(),
                e.componentFieldId(),
                e.status().name(), e.createdAt());
    }
}
