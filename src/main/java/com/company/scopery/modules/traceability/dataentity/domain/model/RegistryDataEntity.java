package com.company.scopery.modules.traceability.dataentity.domain.model;
import com.company.scopery.modules.traceability.dataentity.domain.enums.RegistryDataEntityStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryDataEntity(UUID id, UUID applicationId, UUID workspaceId, UUID moduleId, String code, String name, String description,
                                 String tableName, RegistryDataEntityStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryDataEntity create(UUID applicationId, UUID workspaceId, UUID moduleId, String code, String name, String description, String tableName) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryDataEntity(UUID.randomUUID(), applicationId, workspaceId, moduleId, code, name, description, tableName,
                RegistryDataEntityStatus.ACTIVE, 0, null, null);
    }
    public RegistryDataEntity withUpdated(UUID moduleId, String name, String description, String tableName) {
        return new RegistryDataEntity(id, applicationId, workspaceId, moduleId, code, name, description, tableName, status, version, createdAt, Instant.now());
    }
}
