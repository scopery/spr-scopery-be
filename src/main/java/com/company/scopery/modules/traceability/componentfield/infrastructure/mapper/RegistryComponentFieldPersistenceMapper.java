package com.company.scopery.modules.traceability.componentfield.infrastructure.mapper;

import com.company.scopery.modules.traceability.componentfield.domain.enums.RegistryComponentFieldStatus;
import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentField;
import com.company.scopery.modules.traceability.componentfield.infrastructure.persistence.RegistryComponentFieldJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryComponentFieldPersistenceMapper {

    public RegistryComponentField toDomain(RegistryComponentFieldJpaEntity e) {
        return new RegistryComponentField(
                e.getId(), e.getComponentId(), e.getWorkspaceId(),
                e.getFieldKey(), e.getLabel(), e.getFieldType(),
                e.isRequired(), e.getMaxLength(), e.getRemark(), e.getDisplayOrder(),
                RegistryComponentFieldStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public RegistryComponentFieldJpaEntity toJpaEntity(RegistryComponentField d) {
        RegistryComponentFieldJpaEntity e = new RegistryComponentFieldJpaEntity();
        e.setId(d.id());
        e.setComponentId(d.componentId());
        e.setWorkspaceId(d.workspaceId());
        e.setFieldKey(d.fieldKey());
        e.setLabel(d.label());
        e.setFieldType(d.fieldType());
        e.setRequired(d.required());
        e.setMaxLength(d.maxLength());
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
