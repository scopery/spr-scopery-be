package com.company.scopery.modules.scope.review.infrastructure.mapper;
import com.company.scopery.modules.scope.review.domain.enums.AcceptanceOutcome;
import com.company.scopery.modules.scope.review.domain.model.DeliverableAcceptance;
import com.company.scopery.modules.scope.review.infrastructure.persistence.DeliverableAcceptanceJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeliverableAcceptancePersistenceMapper {
    public DeliverableAcceptance toDomain(DeliverableAcceptanceJpaEntity e) {
        return new DeliverableAcceptance(e.getId(), e.getDeliverableId(), e.getProjectId(),
                AcceptanceOutcome.valueOf(e.getOutcome()), e.getNotes(), e.getAcceptedBy(), e.getAcceptedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public DeliverableAcceptanceJpaEntity toJpaEntity(DeliverableAcceptance d) {
        DeliverableAcceptanceJpaEntity e = new DeliverableAcceptanceJpaEntity();
        e.setId(d.id()); e.setDeliverableId(d.deliverableId()); e.setProjectId(d.projectId());
        e.setOutcome(d.outcome().name()); e.setNotes(d.notes()); e.setAcceptedBy(d.acceptedBy());
        e.setAcceptedAt(d.acceptedAt()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
