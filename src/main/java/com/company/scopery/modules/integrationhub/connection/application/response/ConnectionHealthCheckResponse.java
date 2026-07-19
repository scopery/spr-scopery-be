package com.company.scopery.modules.integrationhub.connection.application.response;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheck;
import java.time.Instant; import java.util.UUID;
public record ConnectionHealthCheckResponse(UUID id, UUID connectionId, String healthStatus, Instant checkedAt,
        Long durationMs, String message, String errorCode) {
    public static ConnectionHealthCheckResponse from(ConnectionHealthCheck c) {
        return new ConnectionHealthCheckResponse(c.id(), c.connectionId(), c.healthStatus(), c.checkedAt(),
                c.durationMs(), c.message(), c.errorCode());
    }
}
