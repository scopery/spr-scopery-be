package com.company.scopery.modules.traceability.appcomponent.infrastructure.mapper;
import com.company.scopery.modules.traceability.appcomponent.domain.enums.RegistryAppComponentStatus;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence.RegistryAppComponentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryAppComponentPersistenceMapper {
    public RegistryAppComponent toDomain(RegistryAppComponentJpaEntity e) {
        return new RegistryAppComponent(e.getId(), e.getApplicationId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getDescription(),
                e.getComponentType(), RegistryAppComponentStatus.valueOf(e.getStatus()), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryAppComponentJpaEntity toJpaEntity(RegistryAppComponent d) {
        RegistryAppComponentJpaEntity e = new RegistryAppComponentJpaEntity();
        e.setId(d.id()); e.setApplicationId(d.applicationId()); e.setWorkspaceId(d.workspaceId());
        e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setComponentType(d.componentType()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
