package com.company.scopery.modules.traceability.appcomponent.domain.model;
import com.company.scopery.modules.traceability.appcomponent.domain.enums.RegistryAppComponentStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryAppComponent(
        UUID id, UUID applicationId, UUID workspaceId, String code, String name, String description,
        String componentType, String optionSourceType,
        UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson,
        String screenshotObjectKey,
        RegistryAppComponentStatus status, int version, Instant createdAt, Instant updatedAt) {

    public static RegistryAppComponent create(UUID applicationId, UUID workspaceId, String code, String name,
            String description, String componentType, String optionSourceType,
            UUID sourceEntityId, String sourceValueColumn, String sourceLabelColumn, String sourceFilterJson) {
        return new RegistryAppComponent(UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                componentType, optionSourceType != null ? optionSourceType : "NONE",
                sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                null,
                RegistryAppComponentStatus.ACTIVE, 0, null, null);
    }

    public RegistryAppComponent withUpdated(String name, String description, String componentType,
            String optionSourceType, UUID sourceEntityId, String sourceValueColumn,
            String sourceLabelColumn, String sourceFilterJson) {
        return new RegistryAppComponent(id, applicationId, workspaceId, code, name, description,
                componentType, optionSourceType != null ? optionSourceType : "NONE",
                sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                screenshotObjectKey,
                status, version, createdAt, Instant.now());
    }

    public RegistryAppComponent withScreenshot(String objectKey) {
        return new RegistryAppComponent(id, applicationId, workspaceId, code, name, description,
                componentType, optionSourceType, sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                objectKey, status, version, createdAt, Instant.now());
    }

    public RegistryAppComponent withScreenshotCleared() {
        return new RegistryAppComponent(id, applicationId, workspaceId, code, name, description,
                componentType, optionSourceType, sourceEntityId, sourceValueColumn, sourceLabelColumn, sourceFilterJson,
                null, status, version, createdAt, Instant.now());
    }
}
