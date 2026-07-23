package com.company.scopery.modules.traceability.appmodule.infrastructure.mapper;
import com.company.scopery.modules.traceability.appmodule.domain.enums.RegistryAppModuleStatus;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.infrastructure.persistence.RegistryAppModuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryAppModulePersistenceMapper {
    public RegistryAppModule toDomain(RegistryAppModuleJpaEntity e) {
        return new RegistryAppModule(e.getId(), e.getApplicationId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getDescription(),
                RegistryAppModuleStatus.valueOf(e.getStatus()), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryAppModuleJpaEntity toJpaEntity(RegistryAppModule d) {
        RegistryAppModuleJpaEntity e = new RegistryAppModuleJpaEntity();
        e.setId(d.id()); e.setApplicationId(d.applicationId()); e.setWorkspaceId(d.workspaceId());
        e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setStatus(d.status().name());
        // New entities: leave version/createdAt null so Persistable.isNew() → persist().
        // Setting version=0 with an assigned id makes Hibernate treat the entity as detached.
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
