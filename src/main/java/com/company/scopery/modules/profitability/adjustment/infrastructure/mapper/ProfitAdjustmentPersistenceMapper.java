package com.company.scopery.modules.profitability.adjustment.infrastructure.mapper;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustment;
import com.company.scopery.modules.profitability.adjustment.infrastructure.persistence.ProfitAdjustmentJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProfitAdjustmentPersistenceMapper {
    public ProfitAdjustment toDomain(ProfitAdjustmentJpaEntity e) {
        return new ProfitAdjustment(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getProfileId(), e.getAdjustmentType(), e.getAmount(),
                e.getReason(), e.getStatus(), e.getSourceLinkType(), e.getSourceLinkId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProfitAdjustmentJpaEntity toJpa(ProfitAdjustment d) {
        ProfitAdjustmentJpaEntity e = new ProfitAdjustmentJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setProfileId(d.profileId());
        e.setAdjustmentType(d.adjustmentType()); e.setAmount(d.amount()); e.setReason(d.reason()); e.setStatus(d.status());
        e.setSourceLinkType(d.sourceLinkType()); e.setSourceLinkId(d.sourceLinkId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
