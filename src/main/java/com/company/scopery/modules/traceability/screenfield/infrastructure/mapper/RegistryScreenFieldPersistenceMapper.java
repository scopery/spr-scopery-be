package com.company.scopery.modules.traceability.screenfield.infrastructure.mapper;
import com.company.scopery.modules.traceability.screenfield.domain.enums.RegistryScreenFieldStatus;
import com.company.scopery.modules.traceability.screenfield.domain.model.RegistryScreenField;
import com.company.scopery.modules.traceability.screenfield.infrastructure.persistence.RegistryScreenFieldJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryScreenFieldPersistenceMapper {
    public RegistryScreenField toDomain(RegistryScreenFieldJpaEntity e) {
        return new RegistryScreenField(e.getId(), e.getScreenId(), e.getSectionId(), e.getWorkspaceId(),
                e.getFieldKey(), e.getLabel(), e.getFieldType(), e.getDescription(), e.isRequired(),
                e.getDisplayOrder(), RegistryScreenFieldStatus.valueOf(e.getStatus()),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RegistryScreenFieldJpaEntity toJpaEntity(RegistryScreenField d) {
        RegistryScreenFieldJpaEntity e = new RegistryScreenFieldJpaEntity();
        e.setId(d.id()); e.setScreenId(d.screenId()); e.setSectionId(d.sectionId());
        e.setWorkspaceId(d.workspaceId()); e.setFieldKey(d.fieldKey()); e.setLabel(d.label());
        e.setFieldType(d.fieldType()); e.setDescription(d.description()); e.setRequired(d.required());
        e.setDisplayOrder(d.displayOrder()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
