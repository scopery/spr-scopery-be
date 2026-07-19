package com.company.scopery.modules.integrationhub.connection.domain.model;
import java.time.Instant; import java.util.UUID;
public record ConnectionHealthCheck(UUID id, UUID workspaceId, UUID connectionId, String healthStatus,
        Instant checkedAt, Long durationMs, String message, String errorCode,
        int version, Instant createdAt, Instant updatedAt) {

    public static ConnectionHealthCheck record(UUID workspaceId, UUID connectionId, String status,
            Long durationMs, String message) {
        Instant now = Instant.now();
        return new ConnectionHealthCheck(UUID.randomUUID(), workspaceId, connectionId, status,
                now, durationMs, message, null, 0, now, now);
    }
}
