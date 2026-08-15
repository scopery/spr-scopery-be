package com.company.scopery.modules.traceability.componentapi.domain.model;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;

import java.time.Instant;
import java.util.UUID;

public record RegistryComponentApi(
        UUID id, UUID componentId, UUID apiId, UUID workspaceId,
        ComponentApiRole role, String note, int displayOrder,
        String status, int version, Instant createdAt, Instant updatedAt) {

    public static RegistryComponentApi create(UUID componentId, UUID apiId, UUID workspaceId,
                                               ComponentApiRole role, String note, int displayOrder) {
        return new RegistryComponentApi(UUID.randomUUID(), componentId, apiId, workspaceId,
                role, note, displayOrder, "ACTIVE", 0, null, null);
    }

    public RegistryComponentApi withUpdated(ComponentApiRole role, String note, int displayOrder) {
        return new RegistryComponentApi(id, componentId, apiId, workspaceId,
                role, note, displayOrder, status, version, createdAt, Instant.now());
    }
}
