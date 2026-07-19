package com.company.scopery.modules.clientportal.policy.infrastructure.mapper;
import com.company.scopery.modules.clientportal.policy.domain.model.ExternalPortalPermissionPolicy;
import com.company.scopery.modules.clientportal.policy.infrastructure.persistence.ExternalPortalPermissionPolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ExternalPortalPermissionPolicyPersistenceMapper {
    public ExternalPortalPermissionPolicy toDomain(ExternalPortalPermissionPolicyJpaEntity e) {
        return new ExternalPortalPermissionPolicy(e.getId(), e.getWorkspaceId(), e.getCode(), e.getName(),
                e.getDescription(), e.getPermissionsJson(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ExternalPortalPermissionPolicyJpaEntity toJpaEntity(ExternalPortalPermissionPolicy d) {
        ExternalPortalPermissionPolicyJpaEntity e = new ExternalPortalPermissionPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setName(d.name());
        e.setDescription(d.description()); e.setPermissionsJson(d.permissionsJson()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
