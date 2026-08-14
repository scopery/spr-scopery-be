package com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.mapper;

import com.company.scopery.modules.traceability.fieldmodeconfig.domain.model.RegistryScreenFieldModeConfig;
import com.company.scopery.modules.traceability.fieldmodeconfig.infrastructure.persistence.RegistryScreenFieldModeConfigJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenFieldModeConfigPersistenceMapper {

    public RegistryScreenFieldModeConfig toDomain(RegistryScreenFieldModeConfigJpaEntity e) {
        return new RegistryScreenFieldModeConfig(
                e.getId(),
                e.getFieldId(),
                e.getModeId(),
                e.getWorkspaceId(),
                e.isVisible(),
                e.isRequired(),
                e.isReadonly(),
                e.getDefaultValue(),
                e.getDisplayOrder(),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenFieldModeConfigJpaEntity toJpaEntity(RegistryScreenFieldModeConfig d) {
        RegistryScreenFieldModeConfigJpaEntity e = new RegistryScreenFieldModeConfigJpaEntity();
        e.setId(d.id());
        e.setFieldId(d.fieldId());
        e.setModeId(d.modeId());
        e.setWorkspaceId(d.workspaceId());
        e.setVisible(d.isVisible());
        e.setRequired(d.isRequired());
        e.setReadonly(d.isReadonly());
        e.setDefaultValue(d.defaultValue());
        e.setDisplayOrder(d.displayOrder());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
