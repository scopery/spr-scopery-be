package com.company.scopery.modules.traceability.funcitemanchor.infrastructure.mapper;

import com.company.scopery.modules.traceability.funcitemanchor.domain.enums.AnchorNodeType;
import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchor;
import com.company.scopery.modules.traceability.funcitemanchor.infrastructure.persistence.FunctionalItemAnchorJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionalItemAnchorPersistenceMapper {

    public FunctionalItemAnchor toDomain(FunctionalItemAnchorJpaEntity e) {
        return new FunctionalItemAnchor(
                e.getId(),
                e.getFunctionalItemId(),
                e.getNodeType() != null ? AnchorNodeType.valueOf(e.getNodeType()) : null,
                e.getNodeId(),
                e.getNote(),
                e.getCreatedAt()
        );
    }

    public FunctionalItemAnchorJpaEntity toJpaEntity(FunctionalItemAnchor d) {
        FunctionalItemAnchorJpaEntity e = new FunctionalItemAnchorJpaEntity();
        e.setId(d.id());
        e.setFunctionalItemId(d.functionalItemId());
        e.setNodeType(d.nodeType() != null ? d.nodeType().name() : null);
        e.setNodeId(d.nodeId());
        e.setNote(d.note());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
