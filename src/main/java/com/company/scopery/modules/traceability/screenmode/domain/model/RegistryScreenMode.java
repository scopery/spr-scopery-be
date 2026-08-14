package com.company.scopery.modules.traceability.screenmode.domain.model;

import com.company.scopery.modules.traceability.screenmode.domain.enums.RegistryScreenModeStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenMode(
        UUID id,
        UUID screenId,
        UUID workspaceId,
        String modeCode,
        String name,
        int displayOrder,
        RegistryScreenModeStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenMode create(UUID screenId, UUID workspaceId, String modeCode, String name, int displayOrder) {
        return new RegistryScreenMode(UUID.randomUUID(), screenId, workspaceId, modeCode, name, displayOrder,
                RegistryScreenModeStatus.ACTIVE, 0, null, null);
    }

    public RegistryScreenMode withUpdated(String name, int displayOrder) {
        return new RegistryScreenMode(id, screenId, workspaceId, modeCode, name, displayOrder, status, version,
                createdAt, Instant.now());
    }
}
