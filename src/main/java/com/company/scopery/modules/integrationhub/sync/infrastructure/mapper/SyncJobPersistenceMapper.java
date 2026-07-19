package com.company.scopery.modules.integrationhub.sync.infrastructure.mapper;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJob;
import com.company.scopery.modules.integrationhub.sync.infrastructure.persistence.SyncJobJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SyncJobPersistenceMapper {
    public SyncJobJpaEntity toJpaEntity(SyncJob d) {
        SyncJobJpaEntity e = new SyncJobJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setConnectionId(d.connectionId()); e.setMappingProfileId(d.mappingProfileId());
        e.setSyncCode(d.syncCode()); e.setName(d.name()); e.setSyncDirection(d.syncDirection());
        e.setSyncMode(d.syncMode()); e.setObjectScope(d.objectScope()); e.setConflictStrategy(d.conflictStrategy());
        e.setScheduleJson(d.scheduleJson()); e.setStatus(d.status());
        e.setDisabledAt(d.disabledAt()); e.setArchivedAt(d.archivedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SyncJob toDomain(SyncJobJpaEntity e) {
        return new SyncJob(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getConnectionId(), e.getMappingProfileId(),
                e.getSyncCode(), e.getName(), e.getSyncDirection(), e.getSyncMode(), e.getObjectScope(), e.getConflictStrategy(),
                e.getScheduleJson(), e.getStatus(), e.getDisabledAt(), e.getArchivedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
