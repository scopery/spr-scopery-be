package com.company.scopery.modules.configuration.fieldoption.infrastructure.mapper;
import com.company.scopery.modules.configuration.fieldoption.domain.model.CustomFieldOption;
import com.company.scopery.modules.configuration.fieldoption.infrastructure.persistence.CustomFieldOptionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class CustomFieldOptionPersistenceMapper {
    public CustomFieldOption toDomain(CustomFieldOptionJpaEntity e) {
        return new CustomFieldOption(e.getId(), e.getCustomFieldDefinitionId(), e.getOptionCode(), e.getLabel(), e.getSortOrder(),
                e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public CustomFieldOptionJpaEntity toJpaEntity(CustomFieldOption d) {
        CustomFieldOptionJpaEntity e = new CustomFieldOptionJpaEntity();
        e.setId(d.id()); e.setCustomFieldDefinitionId(d.customFieldDefinitionId()); e.setOptionCode(d.optionCode());
        e.setLabel(d.label()); e.setSortOrder(d.sortOrder()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
