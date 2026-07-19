package com.company.scopery.modules.profitability.threshold.infrastructure.mapper;
import com.company.scopery.modules.profitability.threshold.domain.model.ProfitThresholdPolicy;
import com.company.scopery.modules.profitability.threshold.infrastructure.persistence.ProfitThresholdPolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProfitThresholdPolicyPersistenceMapper {
    public ProfitThresholdPolicy toDomain(ProfitThresholdPolicyJpaEntity e) {
        return new ProfitThresholdPolicy(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getHealthyMarginPercent(), e.getWatchMarginPercent(),
                e.getAtRiskMarginPercent(), e.getLossRiskMarginPercent(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProfitThresholdPolicyJpaEntity toJpa(ProfitThresholdPolicy d) {
        ProfitThresholdPolicyJpaEntity e = new ProfitThresholdPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setHealthyMarginPercent(d.healthyMarginPercent()); e.setWatchMarginPercent(d.watchMarginPercent());
        e.setAtRiskMarginPercent(d.atRiskMarginPercent()); e.setLossRiskMarginPercent(d.lossRiskMarginPercent()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
