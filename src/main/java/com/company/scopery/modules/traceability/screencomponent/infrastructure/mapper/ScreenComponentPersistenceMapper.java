package com.company.scopery.modules.traceability.screencomponent.infrastructure.mapper;

import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;
import com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence.ScreenComponentId;
import com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence.ScreenComponentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ScreenComponentPersistenceMapper {

    public ScreenComponent toDomain(ScreenComponentJpaEntity e) {
        return new ScreenComponent(
                e.getId().getScreenId(),
                e.getId().getComponentId(),
                e.getSectionId(),
                e.getDisplayOrder(),
                e.getNote(),
                e.getCreatedAt());
    }

    public ScreenComponentJpaEntity toJpaEntity(ScreenComponent d) {
        ScreenComponentJpaEntity e = new ScreenComponentJpaEntity();
        e.setId(new ScreenComponentId(d.screenId(), d.componentId()));
        e.setSectionId(d.sectionId());
        e.setDisplayOrder(d.displayOrder());
        e.setNote(d.note());
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy("SYSTEM");
        return e;
    }
}
