package com.company.scopery.modules.estimation.wbsrollup.infrastructure.mapper;

import com.company.scopery.modules.estimation.wbsrollup.domain.model.WbsEstimateRollup;
import com.company.scopery.modules.estimation.wbsrollup.infrastructure.persistence.WbsEstimateRollupJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WbsEstimateRollupPersistenceMapper {
    public WbsEstimateRollup toDomain(WbsEstimateRollupJpaEntity e) {
        return new WbsEstimateRollup(e.getId(), e.getEstimationRunId(), e.getProjectId(), e.getWbsNodeId(),
                e.getParentWbsNodeId(), e.getDepth() == null ? 0 : e.getDepth(),
                e.getTaskCount() == null ? 0 : e.getTaskCount(),
                e.getIncludedTaskCount() == null ? 0 : e.getIncludedTaskCount(),
                e.getUnresolvedTaskCount() == null ? 0 : e.getUnresolvedTaskCount(),
                e.getTotalEstimateHours(), e.getTotalLaborCost(), e.getTotalBillingPreview(),
                e.getCurrencyCode(), e.getCreatedAt());
    }
    public WbsEstimateRollupJpaEntity toJpaEntity(WbsEstimateRollup d) {
        WbsEstimateRollupJpaEntity e = new WbsEstimateRollupJpaEntity();
        e.setId(d.id()); e.setEstimationRunId(d.estimationRunId()); e.setProjectId(d.projectId());
        e.setWbsNodeId(d.wbsNodeId()); e.setParentWbsNodeId(d.parentWbsNodeId()); e.setDepth(d.depth());
        e.setTaskCount(d.taskCount()); e.setIncludedTaskCount(d.includedTaskCount());
        e.setUnresolvedTaskCount(d.unresolvedTaskCount()); e.setTotalEstimateHours(d.totalEstimateHours());
        e.setTotalLaborCost(d.totalLaborCost()); e.setTotalBillingPreview(d.totalBillingPreview());
        e.setCurrencyCode(d.currencyCode());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
