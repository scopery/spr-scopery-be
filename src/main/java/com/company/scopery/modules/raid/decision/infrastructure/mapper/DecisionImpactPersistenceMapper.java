package com.company.scopery.modules.raid.decision.infrastructure.mapper;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpact;
import com.company.scopery.modules.raid.decision.infrastructure.persistence.DecisionImpactJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DecisionImpactPersistenceMapper {
    public DecisionImpact toDomain(DecisionImpactJpaEntity e) {
        return new DecisionImpact(e.getId(), e.getDecisionId(), e.getProjectId(), e.getScopeImpact(), e.getScheduleImpactDays(),
                e.getEstimateHoursImpact(), e.getCostImpact(), e.getRevenueImpact(), e.getMarginImpact(), e.getRiskImpact(),
                e.getDeliverableImpact(), e.getAcceptanceImpact(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public DecisionImpactJpaEntity toJpaEntity(DecisionImpact d) {
        DecisionImpactJpaEntity e = new DecisionImpactJpaEntity();
        e.setId(d.id()); e.setDecisionId(d.decisionId()); e.setProjectId(d.projectId()); e.setScopeImpact(d.scopeImpact());
        e.setScheduleImpactDays(d.scheduleImpactDays()); e.setEstimateHoursImpact(d.estimateHoursImpact());
        e.setCostImpact(d.costImpact()); e.setRevenueImpact(d.revenueImpact()); e.setMarginImpact(d.marginImpact());
        e.setRiskImpact(d.riskImpact()); e.setDeliverableImpact(d.deliverableImpact()); e.setAcceptanceImpact(d.acceptanceImpact());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
}
