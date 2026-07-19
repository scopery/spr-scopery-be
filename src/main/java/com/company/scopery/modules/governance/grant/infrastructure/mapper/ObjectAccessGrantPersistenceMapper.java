package com.company.scopery.modules.governance.grant.infrastructure.mapper;
import com.company.scopery.modules.governance.grant.domain.model.ObjectAccessGrant;
import com.company.scopery.modules.governance.grant.infrastructure.persistence.ObjectAccessGrantJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ObjectAccessGrantPersistenceMapper {
    public ObjectAccessGrant toDomain(ObjectAccessGrantJpaEntity e) {
        return new ObjectAccessGrant(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getObjectTypeCode(), e.getTargetId(),
                e.getGranteeType(), e.getGranteeId(), e.getGrantRole(), e.getStatus(), e.getExpiresAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ObjectAccessGrantJpaEntity toJpa(ObjectAccessGrant d) {
        ObjectAccessGrantJpaEntity e = new ObjectAccessGrantJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setObjectTypeCode(d.objectTypeCode());
        e.setTargetId(d.targetId()); e.setGranteeType(d.granteeType()); e.setGranteeId(d.granteeId()); e.setGrantRole(d.grantRole());
        e.setStatus(d.status()); e.setExpiresAt(d.expiresAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
