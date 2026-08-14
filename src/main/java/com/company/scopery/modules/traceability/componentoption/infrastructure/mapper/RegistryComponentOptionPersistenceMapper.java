package com.company.scopery.modules.traceability.componentoption.infrastructure.mapper;

import com.company.scopery.modules.traceability.componentoption.domain.enums.ComponentOptionStatus;
import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;
import com.company.scopery.modules.traceability.componentoption.infrastructure.persistence.RegistryComponentOptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryComponentOptionPersistenceMapper {

    public RegistryComponentOption toDomain(RegistryComponentOptionJpaEntity e) {
        return new RegistryComponentOption(
                e.getId(),
                e.getComponentId(),
                e.getWorkspaceId(),
                e.getOptionValue(),
                e.getOptionLabel(),
                e.getDisplayOrder(),
                ComponentOptionStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public RegistryComponentOptionJpaEntity toJpaEntity(RegistryComponentOption d) {
        RegistryComponentOptionJpaEntity e = new RegistryComponentOptionJpaEntity();
        e.setId(d.id());
        e.setComponentId(d.componentId());
        e.setWorkspaceId(d.workspaceId());
        e.setOptionValue(d.optionValue());
        e.setOptionLabel(d.optionLabel());
        e.setDisplayOrder(d.displayOrder());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
