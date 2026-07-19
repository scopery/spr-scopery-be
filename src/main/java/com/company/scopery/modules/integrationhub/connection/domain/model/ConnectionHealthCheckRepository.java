package com.company.scopery.modules.integrationhub.connection.domain.model;
import java.util.List; import java.util.UUID;
public interface ConnectionHealthCheckRepository {
    ConnectionHealthCheck save(ConnectionHealthCheck c);
    List<ConnectionHealthCheck> findByConnectionId(UUID connectionId);
    List<ConnectionHealthCheck> findByWorkspaceId(UUID workspaceId);
}
