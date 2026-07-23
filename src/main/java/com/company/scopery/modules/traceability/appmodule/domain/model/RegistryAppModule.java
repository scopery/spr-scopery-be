package com.company.scopery.modules.traceability.appmodule.domain.model;
import com.company.scopery.modules.traceability.appmodule.domain.enums.RegistryAppModuleStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryAppModule(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
                                RegistryAppModuleStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryAppModule create(UUID applicationId, UUID workspaceId, String code, String name, String description) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryAppModule(UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                RegistryAppModuleStatus.ACTIVE, 0, null, null);
    }
    public RegistryAppModule withUpdated(String name, String description) {
        return new RegistryAppModule(id, applicationId, workspaceId, code, name, description, status, version, createdAt, Instant.now());
    }
}
