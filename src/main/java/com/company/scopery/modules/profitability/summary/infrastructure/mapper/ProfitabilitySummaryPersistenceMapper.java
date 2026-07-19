package com.company.scopery.modules.profitability.summary.infrastructure.mapper;
import com.company.scopery.modules.profitability.summary.domain.model.ProjectProfitabilitySummary;
import com.company.scopery.modules.profitability.summary.infrastructure.persistence.ProjectProfitabilitySummaryJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProfitabilitySummaryPersistenceMapper {
    public ProjectProfitabilitySummary toDomain(ProjectProfitabilitySummaryJpaEntity e) {
        return new ProjectProfitabilitySummary(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCurrency(),
                e.getBaselineRevenue(), e.getForecastRevenue(), e.getBaselineCost(), e.getForecastCost(),
                e.getForecastProfit(), e.getForecastMarginPercent(), e.getProfitabilityStatus(), e.getLastSnapshotAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProjectProfitabilitySummaryJpaEntity toJpaEntity(ProjectProfitabilitySummary d) {
        var e = new ProjectProfitabilitySummaryJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setCurrency(d.currency());
        e.setBaselineRevenue(d.baselineRevenue()); e.setForecastRevenue(d.forecastRevenue());
        e.setBaselineCost(d.baselineCost()); e.setForecastCost(d.forecastCost());
        e.setForecastProfit(d.forecastProfit()); e.setForecastMarginPercent(d.forecastMarginPercent());
        e.setProfitabilityStatus(d.profitabilityStatus()); e.setLastSnapshotAt(d.lastSnapshotAt());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
