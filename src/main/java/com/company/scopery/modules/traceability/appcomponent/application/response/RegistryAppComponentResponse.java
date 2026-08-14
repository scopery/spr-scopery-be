package com.company.scopery.modules.traceability.appcomponent.application.response;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import java.time.Instant; import java.util.UUID;
public record RegistryAppComponentResponse(
        UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
        String componentType, String optionSourceType,
        UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson,
        String status, Instant createdAt) {
    public static RegistryAppComponentResponse from(RegistryAppComponent e) {
        return new RegistryAppComponentResponse(e.id(), e.applicationId(), e.workspaceId(), e.code(), e.name(),
                e.description(), e.componentType(), e.optionSourceType(),
                e.sourceEntityId(), e.sourceValueColumn(), e.sourceLabelColumn(), e.sourceFilterJson(),
                e.status().name(), e.createdAt());
    }
}
