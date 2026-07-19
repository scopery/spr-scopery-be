package com.company.scopery.modules.resourcecapacity.resourcerole.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.enums.ResourceRoleStatus;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRole;
import com.company.scopery.modules.resourcecapacity.resourcerole.infrastructure.persistence.ResourceRoleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ResourceRolePersistenceMapper {
    public ResourceRoleJpaEntity toJpaEntity(ResourceRole d) {
        ResourceRoleJpaEntity e = new ResourceRoleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setRoleCode(d.roleCode());
        e.setName(d.name()); e.setDescription(d.description()); e.setDefaultRateCardId(d.defaultRateCardId());
        e.setStatus(d.status().name()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ResourceRole toDomain(ResourceRoleJpaEntity e) {
        return new ResourceRole(e.getId(), e.getWorkspaceId(), e.getRoleCode(), e.getName(), e.getDescription(),
                e.getDefaultRateCardId(), ResourceRoleStatus.valueOf(e.getStatus()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
