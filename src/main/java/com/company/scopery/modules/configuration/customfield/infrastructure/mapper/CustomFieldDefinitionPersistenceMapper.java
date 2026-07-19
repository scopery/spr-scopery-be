package com.company.scopery.modules.configuration.customfield.infrastructure.mapper;
import com.company.scopery.modules.configuration.customfield.domain.enums.*;
import com.company.scopery.modules.configuration.customfield.domain.model.CustomFieldDefinition;
import com.company.scopery.modules.configuration.customfield.infrastructure.persistence.CustomFieldDefinitionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CustomFieldDefinitionPersistenceMapper {
    public CustomFieldDefinition toDomain(CustomFieldDefinitionJpaEntity e) {
        return new CustomFieldDefinition(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getFieldKey(), e.getLabel(),
                CustomFieldType.valueOf(e.getFieldType()), e.isRequiredFlag(), e.isSensitiveFlag(), e.isClientVisible(),
                e.isSearchable(), e.isReportable(), e.isExportable(), e.getDefaultValueJson(), CustomFieldStatus.valueOf(e.getStatus()),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFieldDefinitionJpaEntity toJpaEntity(CustomFieldDefinition d) {
        CustomFieldDefinitionJpaEntity e = new CustomFieldDefinitionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setFieldKey(d.fieldKey());
        e.setLabel(d.label()); e.setFieldType(d.fieldType().name()); e.setRequiredFlag(d.required()); e.setSensitiveFlag(d.sensitive());
        e.setClientVisible(d.clientVisible()); e.setSearchable(d.searchable()); e.setReportable(d.reportable()); e.setExportable(d.exportable());
        e.setDefaultValueJson(d.defaultValueJson()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
