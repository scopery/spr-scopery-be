package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.mapper;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;
import com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence.SpecDocScreenId;
import com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence.SpecDocScreenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SpecDocScreenPersistenceMapper {

    public SpecDocScreen toDomain(SpecDocScreenJpaEntity e) {
        return new SpecDocScreen(
                e.getId().getDocumentId(),
                e.getId().getScreenId(),
                e.getDisplayOrder(),
                e.getNote());
    }

    public SpecDocScreenJpaEntity toJpaEntity(SpecDocScreen d) {
        SpecDocScreenJpaEntity e = new SpecDocScreenJpaEntity();
        e.setId(new SpecDocScreenId(d.documentId(), d.screenId()));
        e.setDisplayOrder(d.displayOrder());
        e.setNote(d.note());
        return e;
    }
}
