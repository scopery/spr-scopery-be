package com.company.scopery.modules.traceability.componentapi.application.response;

import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApi;

import java.time.Instant;
import java.util.UUID;

public record RegistryComponentApiResponse(
        UUID id, UUID componentId, UUID apiId, UUID workspaceId,
        String role, String note, int displayOrder,
        String status, Instant createdAt) {

    public static RegistryComponentApiResponse from(RegistryComponentApi e) {
        return new RegistryComponentApiResponse(
                e.id(), e.componentId(), e.apiId(), e.workspaceId(),
                e.role() != null ? e.role().name() : null,
                e.note(), e.displayOrder(), e.status(), e.createdAt());
    }
}
