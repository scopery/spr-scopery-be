package com.company.scopery.modules.integrationhub.mapping.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.mapping.domain.model.ExternalIdMapping;
import com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence.ExternalIdMappingJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalIdMappingPersistenceMapper {
    public ExternalIdMappingJpaEntity toJpaEntity(ExternalIdMapping d) {
        ExternalIdMappingJpaEntity e = new ExternalIdMappingJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConnectionId(d.connectionId());
        e.setProviderCode(d.providerCode()); e.setExternalObjectType(d.externalObjectType()); e.setExternalId(d.externalId());
        e.setScoperyObjectType(d.scoperyObjectType()); e.setScoperyObjectId(d.scoperyObjectId());
        e.setLastSyncedAt(d.lastSyncedAt()); e.setSyncState(d.syncState());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ExternalIdMapping toDomain(ExternalIdMappingJpaEntity e) {
        return new ExternalIdMapping(e.getId(), e.getWorkspaceId(), e.getConnectionId(), e.getProviderCode(),
                e.getExternalObjectType(), e.getExternalId(), e.getScoperyObjectType(), e.getScoperyObjectId(),
                e.getLastSyncedAt(), e.getSyncState(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
