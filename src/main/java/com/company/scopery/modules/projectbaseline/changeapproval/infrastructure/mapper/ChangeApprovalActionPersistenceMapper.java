package com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.mapper;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.enums.ChangeApprovalActionType;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalAction;
import com.company.scopery.modules.projectbaseline.changeapproval.infrastructure.persistence.ChangeApprovalActionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ChangeApprovalActionPersistenceMapper {
    public ChangeApprovalAction toDomain(ChangeApprovalActionJpaEntity e) {
        return new ChangeApprovalAction(e.getId(), e.getChangeRequestId(),
                ChangeApprovalActionType.valueOf(e.getAction()), e.getActorUserId(), e.getReason(),
                e.getCreatedAt(), e.getTraceId());
    }
    public ChangeApprovalActionJpaEntity toJpaEntity(ChangeApprovalAction d) {
        ChangeApprovalActionJpaEntity e = new ChangeApprovalActionJpaEntity();
        e.setId(d.id()); e.setChangeRequestId(d.changeRequestId()); e.setAction(d.action().name());
        e.setActorUserId(d.actorUserId()); e.setReason(d.reason()); e.setCreatedAt(d.createdAt());
        e.setTraceId(d.traceId());
        return e;
    }
}
