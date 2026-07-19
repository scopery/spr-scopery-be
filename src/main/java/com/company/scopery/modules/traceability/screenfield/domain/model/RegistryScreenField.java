package com.company.scopery.modules.traceability.screenfield.domain.model;
import com.company.scopery.modules.traceability.screenfield.domain.enums.RegistryScreenFieldStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenField(UUID id, UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
                                  String label, String fieldType, String description, boolean required,
                                  int displayOrder, RegistryScreenFieldStatus status, int version,
                                  Instant createdAt, Instant updatedAt) {
    public static RegistryScreenField create(UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
                                             String label, String fieldType, String description,
                                             boolean required, int displayOrder) {
        Instant now = Instant.now();
        return new RegistryScreenField(UUID.randomUUID(), screenId, sectionId, workspaceId, fieldKey, label, fieldType,
                description, required, displayOrder, RegistryScreenFieldStatus.ACTIVE, 0, now, now);
    }
}
