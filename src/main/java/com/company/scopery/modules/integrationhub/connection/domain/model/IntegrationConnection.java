package com.company.scopery.modules.integrationhub.connection.domain.model;
import java.time.Instant; import java.util.UUID;
public record IntegrationConnection(UUID id, UUID workspaceId, String providerCode, String connectionScope, String name,
        UUID credentialReferenceId, String status, String lastHealthStatus, Instant lastHealthCheckedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static IntegrationConnection create(UUID workspaceId, String providerCode, String name, UUID credentialId) {
        Instant now = Instant.now();
        return new IntegrationConnection(UUID.randomUUID(), workspaceId, providerCode, "WORKSPACE", name, credentialId,
                "DRAFT", "UNKNOWN", null, 0, now, now);
    }
    public IntegrationConnection activate() {
        return new IntegrationConnection(id, workspaceId, providerCode, connectionScope, name, credentialReferenceId,
                "ACTIVE", lastHealthStatus, lastHealthCheckedAt, version, createdAt, Instant.now());
    }
    public IntegrationConnection disable() {
        return new IntegrationConnection(id, workspaceId, providerCode, connectionScope, name, credentialReferenceId,
                "DISABLED", lastHealthStatus, lastHealthCheckedAt, version, createdAt, Instant.now());
    }
    public IntegrationConnection archive() {
        return new IntegrationConnection(id, workspaceId, providerCode, connectionScope, name, credentialReferenceId,
                "ARCHIVED", lastHealthStatus, lastHealthCheckedAt, version, createdAt, Instant.now());
    }
    public IntegrationConnection withHealth(String health) {
        return new IntegrationConnection(id, workspaceId, providerCode, connectionScope, name, credentialReferenceId,
                "HEALTHY".equals(health) ? "ACTIVE" : "DEGRADED", health, Instant.now(), version, createdAt, Instant.now());
    }
    public boolean canRunJobs() { return "ACTIVE".equals(status); }
}
