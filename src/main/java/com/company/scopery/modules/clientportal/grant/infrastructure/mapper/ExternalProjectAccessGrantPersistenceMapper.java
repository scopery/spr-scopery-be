package com.company.scopery.modules.clientportal.grant.infrastructure.mapper;
import com.company.scopery.modules.clientportal.grant.domain.enums.PortalGrantStatus;
import com.company.scopery.modules.clientportal.grant.domain.model.ExternalProjectAccessGrant;
import com.company.scopery.modules.clientportal.grant.infrastructure.persistence.ExternalProjectAccessGrantJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalProjectAccessGrantPersistenceMapper {
    public ExternalProjectAccessGrant toDomain(ExternalProjectAccessGrantJpaEntity e) {
        return new ExternalProjectAccessGrant(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getPortalAccountId(), PortalGrantStatus.valueOf(e.getStatus()),
                e.getPermissionPolicyCode(), e.getExpiresAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalProjectAccessGrantJpaEntity toJpaEntity(ExternalProjectAccessGrant d) {
        ExternalProjectAccessGrantJpaEntity e = new ExternalProjectAccessGrantJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setPortalAccountId(d.portalAccountId());
        e.setStatus(d.status().name()); e.setPermissionPolicyCode(d.permissionPolicyCode()); e.setExpiresAt(d.expiresAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
