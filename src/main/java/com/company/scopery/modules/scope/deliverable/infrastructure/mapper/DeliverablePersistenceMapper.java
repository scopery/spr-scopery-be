package com.company.scopery.modules.scope.deliverable.infrastructure.mapper;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableStatus;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
import com.company.scopery.modules.scope.deliverable.domain.model.Deliverable;
import com.company.scopery.modules.scope.deliverable.infrastructure.persistence.DeliverableJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeliverablePersistenceMapper {
    public Deliverable toDomain(DeliverableJpaEntity e) {
        return new Deliverable(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getProjectPhaseId(), e.getScopeItemId(),
                DeliverableType.valueOf(e.getType()), e.getCode(), e.getTitle(), e.getDescription(), e.isAcceptanceRequired(),
                DeliverableStatus.valueOf(e.getStatus()), e.getAcceptedAt(), e.getAcceptedBy(), e.getRejectedAt(), e.getRejectedBy(),
                e.getRejectionReason(), e.getReopenedAt(), e.getReopenedBy(), e.getReopenReason(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DeliverableJpaEntity toJpaEntity(Deliverable d) {
        DeliverableJpaEntity e = new DeliverableJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setProjectPhaseId(d.projectPhaseId());
        e.setScopeItemId(d.scopeItemId()); e.setType(d.type().name()); e.setCode(d.code()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setAcceptanceRequired(d.acceptanceRequired()); e.setStatus(d.status().name());
        e.setAcceptedAt(d.acceptedAt()); e.setAcceptedBy(d.acceptedBy()); e.setRejectedAt(d.rejectedAt()); e.setRejectedBy(d.rejectedBy());
        e.setRejectionReason(d.rejectionReason()); e.setReopenedAt(d.reopenedAt()); e.setReopenedBy(d.reopenedBy());
        e.setReopenReason(d.reopenReason()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
