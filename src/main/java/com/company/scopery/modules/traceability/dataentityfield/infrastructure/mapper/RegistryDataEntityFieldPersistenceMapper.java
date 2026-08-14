package com.company.scopery.modules.traceability.dataentityfield.infrastructure.mapper;

import com.company.scopery.modules.traceability.dataentityfield.domain.enums.RegistryDataEntityFieldStatus;
import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityField;
import com.company.scopery.modules.traceability.dataentityfield.infrastructure.persistence.RegistryDataEntityFieldJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryDataEntityFieldPersistenceMapper {

    public RegistryDataEntityField toDomain(RegistryDataEntityFieldJpaEntity e) {
        return new RegistryDataEntityField(
                e.getId(),
                e.getEntityId(),
                e.getWorkspaceId(),
                e.getColumnName(),
                e.getDataType(),
                e.getMaxLength(),
                e.isNullable(),
                e.isUnique(),
                e.getRemark(),
                e.getDisplayOrder(),
                RegistryDataEntityFieldStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryDataEntityFieldJpaEntity toJpaEntity(RegistryDataEntityField d) {
        RegistryDataEntityFieldJpaEntity e = new RegistryDataEntityFieldJpaEntity();
        e.setId(d.id());
        e.setEntityId(d.entityId());
        e.setWorkspaceId(d.workspaceId());
        e.setColumnName(d.columnName());
        e.setDataType(d.dataType());
        e.setMaxLength(d.maxLength());
        e.setNullable(d.isNullable());
        e.setUnique(d.isUnique());
        e.setRemark(d.remark());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
