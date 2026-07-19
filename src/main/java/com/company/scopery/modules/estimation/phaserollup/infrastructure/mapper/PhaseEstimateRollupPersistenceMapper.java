package com.company.scopery.modules.estimation.phaserollup.infrastructure.mapper;

import com.company.scopery.modules.estimation.phaserollup.domain.model.PhaseEstimateRollup;
import com.company.scopery.modules.estimation.phaserollup.infrastructure.persistence.PhaseEstimateRollupJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PhaseEstimateRollupPersistenceMapper {
    public PhaseEstimateRollup toDomain(PhaseEstimateRollupJpaEntity e) {
        return new PhaseEstimateRollup(e.getId(), e.getEstimationRunId(), e.getProjectId(), e.getProjectPhaseId(),
                nz(e.getTaskCount()), nz(e.getIncludedTaskCount()), nz(e.getUnresolvedTaskCount()),
                e.getTotalEstimateHours(), e.getTotalLaborCost(), e.getTotalBillingPreview(),
                e.getCurrencyCode(), e.getCreatedAt());
    }
    public PhaseEstimateRollupJpaEntity toJpaEntity(PhaseEstimateRollup d) {
        PhaseEstimateRollupJpaEntity e = new PhaseEstimateRollupJpaEntity();
        e.setId(d.id()); e.setEstimationRunId(d.estimationRunId()); e.setProjectId(d.projectId());
        e.setProjectPhaseId(d.projectPhaseId()); e.setTaskCount(d.taskCount());
        e.setIncludedTaskCount(d.includedTaskCount()); e.setUnresolvedTaskCount(d.unresolvedTaskCount());
        e.setTotalEstimateHours(d.totalEstimateHours()); e.setTotalLaborCost(d.totalLaborCost());
        e.setTotalBillingPreview(d.totalBillingPreview()); e.setCurrencyCode(d.currencyCode());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
    private static int nz(Integer v) { return v == null ? 0 : v; }
}
