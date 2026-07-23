package com.company.scopery.modules.traceability.nfrscope.infrastructure.mapper;

import com.company.scopery.modules.traceability.nfrscope.domain.enums.NfrTargetType;
import com.company.scopery.modules.traceability.nfrscope.domain.model.NfrScopeTarget;
import com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence.NfrScopeTargetId;
import com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence.NfrScopeTargetJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class NfrScopeTargetPersistenceMapper {

    public NfrScopeTarget toDomain(NfrScopeTargetJpaEntity e) {
        return new NfrScopeTarget(
                e.getId().getNfrId(),
                e.getId().getTargetId(),
                e.getTargetType() != null ? NfrTargetType.valueOf(e.getTargetType()) : null,
                e.getCreatedAt()
        );
    }

    public NfrScopeTargetJpaEntity toJpaEntity(NfrScopeTarget d) {
        NfrScopeTargetJpaEntity e = new NfrScopeTargetJpaEntity();
        e.setId(new NfrScopeTargetId(d.nfrId(), d.targetId()));
        e.setTargetType(d.targetType() != null ? d.targetType().name() : null);
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy("SYSTEM");
        return e;
    }
}
