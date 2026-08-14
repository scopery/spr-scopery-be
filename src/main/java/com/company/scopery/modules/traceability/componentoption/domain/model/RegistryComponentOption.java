package com.company.scopery.modules.traceability.componentoption.domain.model;

import com.company.scopery.modules.traceability.componentoption.domain.enums.ComponentOptionStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryComponentOption(
        UUID id,
        UUID componentId,
        UUID workspaceId,
        String optionValue,
        String optionLabel,
        int displayOrder,
        ComponentOptionStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static RegistryComponentOption create(UUID componentId, UUID workspaceId,
                                                  String optionValue, String optionLabel, int displayOrder) {
        return new RegistryComponentOption(UUID.randomUUID(), componentId, workspaceId,
                optionValue, optionLabel, displayOrder, ComponentOptionStatus.ACTIVE, 0, null, null);
    }

    public RegistryComponentOption withUpdated(String optionValue, String optionLabel, int displayOrder) {
        return new RegistryComponentOption(id, componentId, workspaceId,
                optionValue, optionLabel, displayOrder, status, version, createdAt, Instant.now());
    }
}
