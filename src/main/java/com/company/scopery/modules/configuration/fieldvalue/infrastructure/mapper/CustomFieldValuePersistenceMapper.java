package com.company.scopery.modules.configuration.fieldvalue.infrastructure.mapper;
import com.company.scopery.modules.configuration.fieldvalue.domain.model.CustomFieldValue;
import com.company.scopery.modules.configuration.fieldvalue.infrastructure.persistence.CustomFieldValueJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CustomFieldValuePersistenceMapper {
    public CustomFieldValue toDomain(CustomFieldValueJpaEntity e) {
        return new CustomFieldValue(e.getId(), e.getWorkspaceId(), e.getObjectTypeCode(), e.getTargetId(), e.getCustomFieldDefinitionId(),
                e.getValueText(), e.getValueLongText(), e.getValueNumber(), e.getValueDecimal(), e.getValueBoolean(),
                e.getValueDate(), e.getValueDatetime(), e.getValueJson(), e.getValueOptionIds(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFieldValueJpaEntity toJpaEntity(CustomFieldValue d) {
        CustomFieldValueJpaEntity e = new CustomFieldValueJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setObjectTypeCode(d.objectTypeCode()); e.setTargetId(d.targetId());
        e.setCustomFieldDefinitionId(d.customFieldDefinitionId()); e.setValueText(d.valueText()); e.setValueLongText(d.valueLongText());
        e.setValueNumber(d.valueNumber()); e.setValueDecimal(d.valueDecimal()); e.setValueBoolean(d.valueBoolean());
        e.setValueDate(d.valueDate()); e.setValueDatetime(d.valueDatetime()); e.setValueJson(d.valueJson());
        e.setValueOptionIds(d.valueOptionIds()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
