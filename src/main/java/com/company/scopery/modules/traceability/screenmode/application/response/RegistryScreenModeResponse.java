package com.company.scopery.modules.traceability.screenmode.application.response;

import com.company.scopery.modules.traceability.screenmode.domain.model.RegistryScreenMode;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenModeResponse(
        UUID id,
        UUID screenId,
        UUID workspaceId,
        String modeCode,
        String name,
        int displayOrder,
        String status,
        Instant createdAt) {

    public static RegistryScreenModeResponse from(RegistryScreenMode m) {
        return new RegistryScreenModeResponse(
                m.id(),
                m.screenId(),
                m.workspaceId(),
                m.modeCode(),
                m.name(),
                m.displayOrder(),
                m.status().name(),
                m.createdAt());
    }
}
