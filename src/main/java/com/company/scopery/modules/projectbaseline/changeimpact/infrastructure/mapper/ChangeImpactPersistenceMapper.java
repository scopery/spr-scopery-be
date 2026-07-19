package com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.mapper;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpact;
import com.company.scopery.modules.projectbaseline.changeimpact.infrastructure.persistence.ChangeImpactJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ChangeImpactPersistenceMapper {
    public ChangeImpact toDomain(ChangeImpactJpaEntity e) {
        return new ChangeImpact(e.getId(), e.getChangeRequestId(), e.getProjectId(), e.getCurrencyCode(),
                e.getScopeImpact(), e.getScheduleImpactDays(), e.getEstimateHoursImpact(), e.getLaborCostImpact(),
                e.getDirectCostImpact(), e.getOverheadImpact(), e.getRevenueImpact(), e.getGrossMarginImpact(),
                e.getPbtImpact(), e.getQuoteAmountImpact(), e.getRiskImpact(), e.getImpactSummaryJson(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ChangeImpactJpaEntity toJpaEntity(ChangeImpact d) {
        ChangeImpactJpaEntity e = new ChangeImpactJpaEntity();
        e.setId(d.id()); e.setChangeRequestId(d.changeRequestId()); e.setProjectId(d.projectId());
        e.setCurrencyCode(d.currencyCode()); e.setScopeImpact(d.scopeImpact());
        e.setScheduleImpactDays(d.scheduleImpactDays()); e.setEstimateHoursImpact(d.estimateHoursImpact());
        e.setLaborCostImpact(d.laborCostImpact()); e.setDirectCostImpact(d.directCostImpact());
        e.setOverheadImpact(d.overheadImpact()); e.setRevenueImpact(d.revenueImpact());
        e.setGrossMarginImpact(d.grossMarginImpact()); e.setPbtImpact(d.pbtImpact());
        e.setQuoteAmountImpact(d.quoteAmountImpact()); e.setRiskImpact(d.riskImpact());
        e.setImpactSummaryJson(d.impactSummaryJson()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
