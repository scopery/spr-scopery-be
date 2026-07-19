package com.company.scopery.modules.traceability.appcomponent.domain.model;
import com.company.scopery.modules.traceability.appcomponent.domain.enums.RegistryAppComponentStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryAppComponent(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
                                   String componentType, RegistryAppComponentStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryAppComponent create(UUID applicationId, UUID workspaceId, String code, String name, String description, String componentType) {
        Instant now = Instant.now();
        return new RegistryAppComponent(UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                componentType, RegistryAppComponentStatus.ACTIVE, 0, now, now);
    }
}
