package com.company.scopery.modules.traceability.appcomponent.domain.model;
import com.company.scopery.modules.traceability.appcomponent.domain.enums.RegistryAppComponentStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryAppComponent(
        UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
        String componentType, String optionSourceType,
        UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson,
        RegistryAppComponentStatus status, int version, Instant createdAt, Instant updatedAt) {

    public static RegistryAppComponent create(UUID applicationId, UUID workspaceId, String code, String name,
            String description, String componentType, String optionSourceType,
            UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson) {
        return new RegistryAppComponent(UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                componentType, optionSourceType != null ? optionSourceType : "NONE",
                sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                RegistryAppComponentStatus.ACTIVE, 0, null, null);
    }

    public RegistryAppComponent withUpdated(String name, String description, String componentType,
            String optionSourceType, UUID sourceEntityId, String sourceValueColumn,
            String sourceLabelColumn, String sourceFilterJson) {
        return new RegistryAppComponent(id, applicationId, workspaceId, code, name, description,
                componentType, optionSourceType != null ? optionSourceType : "NONE",
                sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                status, version, createdAt, Instant.now());
    }
}
