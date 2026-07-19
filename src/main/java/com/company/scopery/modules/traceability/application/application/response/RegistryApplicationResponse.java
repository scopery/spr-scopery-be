package com.company.scopery.modules.traceability.application.application.response;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplication;
import java.time.Instant; import java.util.UUID;
public record RegistryApplicationResponse(UUID id, UUID workspaceId, String code, String name, String status, Instant createdAt) {
    public static RegistryApplicationResponse from(RegistryApplication e) {
        return new RegistryApplicationResponse(e.id(), e.workspaceId(), e.code(), e.name(), e.status().name(), e.createdAt());
    }
}
