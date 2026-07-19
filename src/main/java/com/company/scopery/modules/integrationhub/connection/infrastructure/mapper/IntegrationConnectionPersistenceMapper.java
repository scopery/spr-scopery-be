package com.company.scopery.modules.integrationhub.connection.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import com.company.scopery.modules.integrationhub.connection.infrastructure.persistence.IntegrationConnectionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class IntegrationConnectionPersistenceMapper {
    public IntegrationConnectionJpaEntity toJpaEntity(IntegrationConnection d) {
        IntegrationConnectionJpaEntity e = new IntegrationConnectionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProviderCode(d.providerCode());
        e.setConnectionScope(d.connectionScope()); e.setName(d.name()); e.setCredentialReferenceId(d.credentialReferenceId());
        e.setStatus(d.status()); e.setLastHealthStatus(d.lastHealthStatus()); e.setLastHealthCheckedAt(d.lastHealthCheckedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public IntegrationConnection toDomain(IntegrationConnectionJpaEntity e) {
        return new IntegrationConnection(e.getId(), e.getWorkspaceId(), e.getProviderCode(), e.getConnectionScope(), e.getName(),
                e.getCredentialReferenceId(), e.getStatus(), e.getLastHealthStatus(), e.getLastHealthCheckedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
