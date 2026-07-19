package com.company.scopery.modules.traceability.dataentity.domain.model;
import com.company.scopery.modules.traceability.dataentity.domain.enums.RegistryDataEntityStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryDataEntity(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
                                 String tableName, RegistryDataEntityStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryDataEntity create(UUID applicationId, UUID workspaceId, String code, String name, String description, String tableName) {
        Instant now = Instant.now();
        return new RegistryDataEntity(UUID.randomUUID(), applicationId, workspaceId, code, name, description, tableName,
                RegistryDataEntityStatus.ACTIVE, 0, now, now);
    }
}
