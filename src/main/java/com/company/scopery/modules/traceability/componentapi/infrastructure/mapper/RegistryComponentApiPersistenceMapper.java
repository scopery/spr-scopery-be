package com.company.scopery.modules.traceability.componentapi.infrastructure.mapper;

import com.company.scopery.modules.traceability.componentapi.domain.enums.ComponentApiRole;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApi;
import com.company.scopery.modules.traceability.componentapi.infrastructure.persistence.RegistryComponentApiJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryComponentApiPersistenceMapper {

    public RegistryComponentApi toDomain(RegistryComponentApiJpaEntity e) {
        return new RegistryComponentApi(
                e.getId(), e.getComponentId(), e.getApiId(), e.getWorkspaceId(),
                e.getRole() != null ? ComponentApiRole.valueOf(e.getRole()) : null,
                e.getNote(), e.getDisplayOrder(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public RegistryComponentApiJpaEntity toJpaEntity(RegistryComponentApi d) {
        RegistryComponentApiJpaEntity e = new RegistryComponentApiJpaEntity();
        e.setId(d.id());
        e.setComponentId(d.componentId());
        e.setApiId(d.apiId());
        e.setWorkspaceId(d.workspaceId());
        e.setRole(d.role() != null ? d.role().name() : null);
        e.setNote(d.note());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
