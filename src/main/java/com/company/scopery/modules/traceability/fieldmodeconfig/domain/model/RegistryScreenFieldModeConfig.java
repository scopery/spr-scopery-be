package com.company.scopery.modules.traceability.fieldmodeconfig.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenFieldModeConfig(
        UUID id,
        UUID fieldId,
        UUID modeId,
        UUID workspaceId,
        boolean isVisible,
        boolean isRequired,
        boolean isReadonly,
        String defaultValue,
        Integer displayOrder,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenFieldModeConfig create(UUID fieldId, UUID modeId, UUID workspaceId,
                                                        boolean isVisible, boolean isRequired,
                                                        boolean isReadonly, String defaultValue,
                                                        Integer displayOrder) {
        return new RegistryScreenFieldModeConfig(UUID.randomUUID(), fieldId, modeId, workspaceId,
                isVisible, isRequired, isReadonly, defaultValue, displayOrder, 0, null, null);
    }

    public RegistryScreenFieldModeConfig withUpdated(boolean isVisible, boolean isRequired,
                                                      boolean isReadonly, String defaultValue,
                                                      Integer displayOrder) {
        return new RegistryScreenFieldModeConfig(id, fieldId, modeId, workspaceId,
                isVisible, isRequired, isReadonly, defaultValue, displayOrder,
                version, createdAt, Instant.now());
    }
}
