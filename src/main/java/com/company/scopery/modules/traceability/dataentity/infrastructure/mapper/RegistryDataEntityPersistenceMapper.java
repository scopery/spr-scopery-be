package com.company.scopery.modules.traceability.dataentity.infrastructure.mapper;
import com.company.scopery.modules.traceability.dataentity.domain.enums.RegistryDataEntityStatus;
import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;
import com.company.scopery.modules.traceability.dataentity.infrastructure.persistence.RegistryDataEntityJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryDataEntityPersistenceMapper {
    public RegistryDataEntity toDomain(RegistryDataEntityJpaEntity e) {
        return new RegistryDataEntity(e.getId(), e.getApplicationId(), e.getWorkspaceId(), e.getCode(), e.getName(),
                e.getDescription(), e.getTableName(), RegistryDataEntityStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryDataEntityJpaEntity toJpaEntity(RegistryDataEntity d) {
        RegistryDataEntityJpaEntity e = new RegistryDataEntityJpaEntity();
        e.setId(d.id()); e.setApplicationId(d.applicationId()); e.setWorkspaceId(d.workspaceId());
        e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setTableName(d.tableName()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
