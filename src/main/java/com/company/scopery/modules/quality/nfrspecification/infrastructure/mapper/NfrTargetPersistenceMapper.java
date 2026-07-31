package com.company.scopery.modules.quality.nfrspecification.infrastructure.mapper;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.NfrTargetType;
import com.company.scopery.modules.quality.nfrspecification.domain.model.NfrTarget;
import com.company.scopery.modules.quality.nfrspecification.infrastructure.persistence.NfrTargetJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class NfrTargetPersistenceMapper {
    public NfrTarget toDomain(NfrTargetJpaEntity e) {
        return new NfrTarget(e.getId(), e.getRequirementId(),
                NfrTargetType.valueOf(e.getTargetType()),
                e.getTargetId(), e.getTargetLabel(),
                e.getDisplayOrder(), e.getCreatedAt());
    }
    public NfrTargetJpaEntity toJpaEntity(NfrTarget d) {
        NfrTargetJpaEntity e = new NfrTargetJpaEntity();
        e.setId(d.id()); e.setRequirementId(d.requirementId());
        e.setTargetType(d.targetType().name());
        e.setTargetId(d.targetId()); e.setTargetLabel(d.targetLabel());
        e.setDisplayOrder(d.displayOrder());
        return e;
    }
}
