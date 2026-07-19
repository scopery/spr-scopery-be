package com.company.scopery.modules.raid.decision.infrastructure.mapper;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionCategory;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionStatus;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import com.company.scopery.modules.raid.decision.infrastructure.persistence.DecisionRecordJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DecisionRecordPersistenceMapper {
    public DecisionRecord toDomain(DecisionRecordJpaEntity e) {
        return new DecisionRecord(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getCode(), e.getTitle(),
                DecisionCategory.valueOf(e.getCategory()), DecisionStatus.valueOf(e.getStatus()), e.getRationale(), e.getOutcome(),
                e.getDecidedAt(), e.getDecidedBy(), e.getSupersededByDecisionId(), e.getLinkedChangeRequestId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DecisionRecordJpaEntity toJpaEntity(DecisionRecord d) {
        DecisionRecordJpaEntity e = new DecisionRecordJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code());
        e.setTitle(d.title()); e.setCategory(d.category().name()); e.setStatus(d.status().name());
        e.setRationale(d.rationale()); e.setOutcome(d.outcome()); e.setDecidedAt(d.decidedAt()); e.setDecidedBy(d.decidedBy());
        e.setSupersededByDecisionId(d.supersededByDecisionId()); e.setLinkedChangeRequestId(d.linkedChangeRequestId());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
