package com.company.scopery.modules.traceability.screen.application.response;

import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;
import java.time.Instant;
import java.util.UUID;

public record RegistryScreenResponse(UUID id, UUID applicationId, String code, String name,
                                      String routePath, String status, Instant createdAt) {
    public static RegistryScreenResponse from(RegistryScreen e) {
        return new RegistryScreenResponse(e.id(), e.applicationId(), e.code(), e.name(),
                e.routePath(), e.status().name(), e.createdAt());
    }
}
