package com.company.scopery.modules.integrationhub.connection.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.connection.domain.model.ConnectionHealthCheck;
import com.company.scopery.modules.integrationhub.connection.infrastructure.persistence.ConnectionHealthCheckJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ConnectionHealthCheckPersistenceMapper {
    public ConnectionHealthCheckJpaEntity toJpaEntity(ConnectionHealthCheck d) {
        ConnectionHealthCheckJpaEntity e = new ConnectionHealthCheckJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setHealthStatus(d.healthStatus()); e.setCheckedAt(d.checkedAt()); e.setDurationMs(d.durationMs());
        e.setMessage(d.message()); e.setErrorCode(d.errorCode());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ConnectionHealthCheck toDomain(ConnectionHealthCheckJpaEntity e) {
        return new ConnectionHealthCheck(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getHealthStatus(),
                e.getCheckedAt(), e.getDurationMs(), e.getMessage(), e.getErrorCode(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
