package com.company.scopery.modules.traceability.appcomponent.domain.model;
import com.company.scopery.modules.traceability.appcomponent.domain.enums.RegistryAppComponentStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryAppComponent(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
                                   String componentType, RegistryAppComponentStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryAppComponent create(UUID applicationId, UUID workspaceId, String code, String name, String description, String componentType) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryAppComponent(UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                componentType, RegistryAppComponentStatus.ACTIVE, 0, null, null);
    }
    public RegistryAppComponent withUpdated(String name, String description, String componentType) {
        return new RegistryAppComponent(id, applicationId, workspaceId, code, name, description, componentType, status, version, createdAt, Instant.now());
    }
}
