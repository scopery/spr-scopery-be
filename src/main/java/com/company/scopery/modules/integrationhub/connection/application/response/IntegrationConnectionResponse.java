package com.company.scopery.modules.integrationhub.connection.application.response;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import java.time.Instant; import java.util.UUID;
public record IntegrationConnectionResponse(UUID id, UUID workspaceId, String providerCode, String connectionScope,
        String name, UUID credentialReferenceId, String status, String lastHealthStatus,
        Instant lastHealthCheckedAt, Instant createdAt, Instant updatedAt) {
    public static IntegrationConnectionResponse from(IntegrationConnection c) {
        return new IntegrationConnectionResponse(c.id(), c.workspaceId(), c.providerCode(), c.connectionScope(),
                c.name(), c.credentialReferenceId(), c.status(), c.lastHealthStatus(),
                c.lastHealthCheckedAt(), c.createdAt(), c.updatedAt());
    }
}
