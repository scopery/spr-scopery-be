package com.company.scopery.modules.integrationhub.sync.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflict;
import com.company.scopery.modules.integrationhub.sync.infrastructure.persistence.SyncConflictJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SyncConflictPersistenceMapper {
    public SyncConflictJpaEntity toJpaEntity(SyncConflict d) {
        SyncConflictJpaEntity e = new SyncConflictJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSyncJobId(d.syncJobId()); e.setSyncRunId(d.syncRunId());
        e.setConnectionId(d.connectionId()); e.setConflictType(d.conflictType());
        e.setScoperyObjectType(d.scoperyObjectType()); e.setScoperyObjectId(d.scoperyObjectId());
        e.setExternalObjectType(d.externalObjectType()); e.setExternalId(d.externalId());
        e.setSeverity(d.severity()); e.setStatus(d.status()); e.setDescription(d.description());
        e.setResolutionStrategy(d.resolutionStrategy()); e.setResolvedAt(d.resolvedAt()); e.setResolvedBy(d.resolvedBy());
        e.setResolutionNotes(d.resolutionNotes());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SyncConflict toDomain(SyncConflictJpaEntity e) {
        return new SyncConflict(e.getId(), e.getWorkspaceId(), e.getSyncJobId(), e.getSyncRunId(), e.getConnectionId(),
                e.getConflictType(), e.getScoperyObjectType(), e.getScoperyObjectId(),
                e.getExternalObjectType(), e.getExternalId(),
                e.getSeverity(), e.getStatus(), e.getDescription(),
                e.getResolutionStrategy(), e.getResolvedAt(), e.getResolvedBy(), e.getResolutionNotes(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
