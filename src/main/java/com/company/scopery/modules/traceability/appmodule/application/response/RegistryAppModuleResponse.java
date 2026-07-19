package com.company.scopery.modules.traceability.appmodule.application.response;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import java.time.Instant; import java.util.UUID;
public record RegistryAppModuleResponse(UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description, String status, Instant createdAt) {
    public static RegistryAppModuleResponse from(RegistryAppModule e) {
        return new RegistryAppModuleResponse(e.id(), e.applicationId(), e.workspaceId(), e.code(), e.name(), e.description(), e.status().name(), e.createdAt());
    }
}
