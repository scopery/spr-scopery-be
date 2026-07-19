package com.company.scopery.modules.profitability.summary.infrastructure.mapper;
import com.company.scopery.modules.profitability.summary.domain.model.ProfitSnapshot;
import com.company.scopery.modules.profitability.summary.infrastructure.persistence.ProfitSnapshotJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProfitSnapshotPersistenceMapper {
    public ProfitSnapshot toDomain(ProfitSnapshotJpaEntity e) {
        return new ProfitSnapshot(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getProfileId(),
                e.getBaselineRevenue(), e.getForecastRevenue(), e.getBaselineCost(), e.getForecastCost(),
                e.getForecastProfit(), e.getForecastMarginPercent(), e.getProfitabilityStatus(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProfitSnapshotJpaEntity toJpaEntity(ProfitSnapshot d) {
        var e = new ProfitSnapshotJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setProfileId(d.profileId());
        e.setBaselineRevenue(d.baselineRevenue()); e.setForecastRevenue(d.forecastRevenue());
        e.setBaselineCost(d.baselineCost()); e.setForecastCost(d.forecastCost());
        e.setForecastProfit(d.forecastProfit()); e.setForecastMarginPercent(d.forecastMarginPercent());
        e.setProfitabilityStatus(d.profitabilityStatus()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
