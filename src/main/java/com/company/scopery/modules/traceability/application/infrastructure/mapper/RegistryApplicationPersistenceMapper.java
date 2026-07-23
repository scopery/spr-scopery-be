package com.company.scopery.modules.traceability.application.infrastructure.mapper;
import com.company.scopery.modules.traceability.application.domain.enums.RegistryApplicationStatus;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplication;
import com.company.scopery.modules.traceability.application.infrastructure.persistence.RegistryApplicationJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryApplicationPersistenceMapper {
    public RegistryApplication toDomain(RegistryApplicationJpaEntity e) {
        return new RegistryApplication(e.getId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getDescription(),
                RegistryApplicationStatus.valueOf(e.getStatus()), e.getOwnerUserId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryApplicationJpaEntity toJpaEntity(RegistryApplication d) {
        RegistryApplicationJpaEntity e = new RegistryApplicationJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setName(d.name());
        e.setDescription(d.description()); e.setStatus(d.status().name()); e.setOwnerUserId(d.ownerUserId());
        // Only stamp audit/version on updates — null createdAt keeps Persistable.isNew() true for INSERT.
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
