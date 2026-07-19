package com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.mapper;

import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfile;
import com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.persistence.ServiceProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceProfilePersistenceMapper {
    public ServiceProfileJpaEntity toJpa(ServiceProfile d) {
        ServiceProfileJpaEntity e = new ServiceProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setScopeType(d.scopeType());
        e.setProjectId(d.projectId()); e.setPortalIntakeEnabled(d.portalIntakeEnabled());
        e.setStatus(d.status()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ServiceProfile toDomain(ServiceProfileJpaEntity e) {
        return new ServiceProfile(e.getId(), e.getWorkspaceId(), e.getScopeType(), e.getProjectId(),
                e.isPortalIntakeEnabled(), e.getStatus(), e.getCreatedAt());
    }
}
