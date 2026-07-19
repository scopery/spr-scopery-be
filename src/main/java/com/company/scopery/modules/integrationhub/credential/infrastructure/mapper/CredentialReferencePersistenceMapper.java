package com.company.scopery.modules.integrationhub.credential.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.credential.domain.model.CredentialReference;
import com.company.scopery.modules.integrationhub.credential.infrastructure.persistence.CredentialReferenceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CredentialReferencePersistenceMapper {
    public CredentialReferenceJpaEntity toJpa(CredentialReference d) {
        CredentialReferenceJpaEntity e = new CredentialReferenceJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProviderCode(d.providerCode());
        e.setCredentialType(d.credentialType()); e.setSecretReference(d.secretReference()); e.setStatus(d.status());
        e.setLastRotatedAt(d.lastRotatedAt()); e.setRevokedAt(d.revokedAt()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public CredentialReference toDomain(CredentialReferenceJpaEntity e) {
        return new CredentialReference(e.getId(), e.getWorkspaceId(), e.getProviderCode(), e.getCredentialType(), e.getSecretReference(),
                e.getStatus(), e.getLastRotatedAt(), e.getRevokedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
