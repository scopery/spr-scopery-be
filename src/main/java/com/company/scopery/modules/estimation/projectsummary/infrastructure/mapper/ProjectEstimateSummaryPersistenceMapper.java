package com.company.scopery.modules.estimation.projectsummary.infrastructure.mapper;

import com.company.scopery.modules.estimation.projectsummary.domain.model.ProjectEstimateSummary;
import com.company.scopery.modules.estimation.projectsummary.infrastructure.persistence.ProjectEstimateSummaryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectEstimateSummaryPersistenceMapper {
    public ProjectEstimateSummary toDomain(ProjectEstimateSummaryJpaEntity e) {
        return new ProjectEstimateSummary(e.getId(), e.getEstimationRunId(), e.getProjectId(),
                nz(e.getTotalTaskCount()), nz(e.getIncludedTaskCount()), nz(e.getExcludedTaskCount()),
                nz(e.getUnestimatedTaskCount()), nz(e.getUnresolvedRoleTaskCount()), nz(e.getUnresolvedRateTaskCount()),
                e.getTotalEstimateHours(), e.getTotalLaborCost(), e.getTotalBillingPreview(),
                e.getAverageCostRate(), e.getAverageBillingRate(), e.getCurrencyCode(),
                nz(e.getWarningCount()), e.getCreatedAt());
    }
    public ProjectEstimateSummaryJpaEntity toJpaEntity(ProjectEstimateSummary d) {
        ProjectEstimateSummaryJpaEntity e = new ProjectEstimateSummaryJpaEntity();
        e.setId(d.id()); e.setEstimationRunId(d.estimationRunId()); e.setProjectId(d.projectId());
        e.setTotalTaskCount(d.totalTaskCount()); e.setIncludedTaskCount(d.includedTaskCount());
        e.setExcludedTaskCount(d.excludedTaskCount()); e.setUnestimatedTaskCount(d.unestimatedTaskCount());
        e.setUnresolvedRoleTaskCount(d.unresolvedRoleTaskCount());
        e.setUnresolvedRateTaskCount(d.unresolvedRateTaskCount());
        e.setTotalEstimateHours(d.totalEstimateHours()); e.setTotalLaborCost(d.totalLaborCost());
        e.setTotalBillingPreview(d.totalBillingPreview()); e.setAverageCostRate(d.averageCostRate());
        e.setAverageBillingRate(d.averageBillingRate()); e.setCurrencyCode(d.currencyCode());
        e.setWarningCount(d.warningCount());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
    private static int nz(Integer v) { return v == null ? 0 : v; }
}
