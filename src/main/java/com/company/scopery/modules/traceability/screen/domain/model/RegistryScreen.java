package com.company.scopery.modules.traceability.screen.domain.model;

import com.company.scopery.modules.traceability.screen.domain.enums.RegistryScreenStatus;
import java.time.Instant;
import java.util.UUID;

public record RegistryScreen(UUID id, UUID applicationId, UUID projectId, String code, String name,
                              String routePath, RegistryScreenStatus status, int version,
                              Instant createdAt, Instant updatedAt) {
    public static RegistryScreen create(UUID applicationId, UUID projectId, String code, String name, String routePath) {
        Instant now = Instant.now();
        return new RegistryScreen(UUID.randomUUID(), applicationId, projectId, code, name, routePath,
                RegistryScreenStatus.ACTIVE, 0, now, now);
    }
}
