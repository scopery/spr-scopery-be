package com.company.scopery.modules.traceability.screen.domain.model;

import com.company.scopery.modules.traceability.screen.domain.enums.RegistryScreenStatus;
import java.time.Instant;
import java.util.UUID;

public record RegistryScreen(UUID id, UUID applicationId, UUID projectId, String code, String name,
                              String routePath, String mockupObjectKey, RegistryScreenStatus status, int version,
                              Instant createdAt, Instant updatedAt) {
    public static RegistryScreen create(UUID applicationId, UUID projectId, String code, String name, String routePath) {
        return new RegistryScreen(UUID.randomUUID(), applicationId, projectId, code, name, routePath, null,
                RegistryScreenStatus.ACTIVE, 0, null, null);
    }
    public RegistryScreen withUpdated(String name, String routePath) {
        return new RegistryScreen(id, applicationId, projectId, code, name, routePath, mockupObjectKey, status, version, createdAt, Instant.now());
    }
    public RegistryScreen withMockup(String objectKey) {
        return new RegistryScreen(id, applicationId, projectId, code, name, routePath, objectKey, status, version, createdAt, Instant.now());
    }
    public RegistryScreen withMockupCleared() {
        return new RegistryScreen(id, applicationId, projectId, code, name, routePath, null, status, version, createdAt, Instant.now());
    }
}
