package com.company.scopery.modules.traceability.fieldmodeconfig.application.response;

import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenFieldModeConfigResponse(
        UUID id,
        UUID fieldId,
        UUID modeId,
        UUID workspaceId,
        boolean isVisible,
        boolean isRequired,
        boolean isReadonly,
        String defaultValue,
        Integer displayOrder,
        Instant createdAt) {

    public static RegistryScreenFieldModeConfigResponse from(RegistryScreenFieldModeConfig c) {
        return new RegistryScreenFieldModeConfigResponse(
                c.id(),
                c.fieldId(),
                c.modeId(),
                c.workspaceId(),
                c.isVisible(),
                c.isRequired(),
                c.isReadonly(),
                c.defaultValue(),
                c.displayOrder(),
                c.createdAt());
    }
}
