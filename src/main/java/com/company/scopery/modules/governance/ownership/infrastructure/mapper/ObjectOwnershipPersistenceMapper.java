package com.company.scopery.modules.governance.ownership.infrastructure.mapper;
import com.company.scopery.modules.governance.ownership.domain.model.ObjectOwnership;
import com.company.scopery.modules.governance.ownership.infrastructure.persistence.ObjectOwnershipJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ObjectOwnershipPersistenceMapper {
    public ObjectOwnership toDomain(ObjectOwnershipJpaEntity e) {
        return new ObjectOwnership(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getTargetId(),
                e.getOwnerTargetType(), e.getOwnerTargetId(), e.getStatus(), e.getAssignedAt(), e.getAssignedBy(), e.getRevokedAt(), e.getRevokedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ObjectOwnershipJpaEntity toJpaEntity(ObjectOwnership d) {
        ObjectOwnershipJpaEntity e = new ObjectOwnershipJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setTargetId(d.targetId()); e.setOwnerTargetType(d.ownerTargetType()); e.setOwnerTargetId(d.ownerTargetId()); e.setStatus(d.status());
        e.setAssignedAt(d.assignedAt()); e.setAssignedBy(d.assignedBy()); e.setRevokedAt(d.revokedAt()); e.setRevokedBy(d.revokedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
