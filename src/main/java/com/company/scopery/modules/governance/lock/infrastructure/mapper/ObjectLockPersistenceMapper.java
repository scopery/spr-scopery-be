package com.company.scopery.modules.governance.lock.infrastructure.mapper;
import com.company.scopery.modules.governance.lock.domain.model.ObjectLock;
import com.company.scopery.modules.governance.lock.infrastructure.persistence.ObjectLockJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ObjectLockPersistenceMapper {
    public ObjectLock toDomain(ObjectLockJpaEntity e) {
        return new ObjectLock(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getTargetId(), e.getLockType(),
                e.getStatus(), e.getLockedAt(), e.getLockedBy(), e.getReleasedAt(), e.getReleasedBy(), e.getReason(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ObjectLockJpaEntity toJpaEntity(ObjectLock d) {
        ObjectLockJpaEntity e = new ObjectLockJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setTargetId(d.targetId()); e.setLockType(d.lockType()); e.setStatus(d.status()); e.setLockedAt(d.lockedAt());
        e.setLockedBy(d.lockedBy()); e.setReleasedAt(d.releasedAt()); e.setReleasedBy(d.releasedBy()); e.setReason(d.reason());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
