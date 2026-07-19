package com.company.scopery.modules.trust.datasubject.infrastructure.mapper;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndex;
import com.company.scopery.modules.trust.datasubject.infrastructure.persistence.DataSubjectIndexJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DataSubjectIndexPersistenceMapper {
    public DataSubjectIndexJpaEntity toJpaEntity(DataSubjectIndex d) {
        DataSubjectIndexJpaEntity e = new DataSubjectIndexJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSubjectType(d.subjectType());
        e.setSubjectId(d.subjectId()); e.setDisplayNameSnapshot(d.displayNameSnapshot());
        e.setLinkedUserId(d.linkedUserId()); e.setLinkedExternalContactId(d.linkedExternalContactId());
        e.setRecordCount(d.recordCount()); e.setLastRebuiltAt(d.lastRebuiltAt());
        e.setStatus(d.status()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public DataSubjectIndex toDomain(DataSubjectIndexJpaEntity e) {
        return new DataSubjectIndex(e.getId(), e.getWorkspaceId(), e.getSubjectType(), e.getSubjectId(),
                e.getDisplayNameSnapshot(), e.getLinkedUserId(), e.getLinkedExternalContactId(),
                e.getRecordCount(), e.getLastRebuiltAt(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
