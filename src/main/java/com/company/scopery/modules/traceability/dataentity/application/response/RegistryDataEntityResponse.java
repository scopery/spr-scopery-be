package com.company.scopery.modules.traceability.dataentity.application.response;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import java.time.Instant; import java.util.UUID;
public record RegistryDataEntityResponse(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description, String tableName, String status, Instant createdAt) {
    public static RegistryDataEntityResponse from(RegistryDataEntity e) {
        return new RegistryDataEntityResponse(e.id(), e.applicationId(), e.workspaceId(), e.code(), e.name(), e.description(), e.tableName(), e.status().name(), e.createdAt());
    }
}
