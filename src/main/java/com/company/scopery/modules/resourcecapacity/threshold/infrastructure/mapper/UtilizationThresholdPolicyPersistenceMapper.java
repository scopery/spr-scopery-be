package com.company.scopery.modules.resourcecapacity.threshold.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.threshold.domain.model.UtilizationThresholdPolicy;
import com.company.scopery.modules.resourcecapacity.threshold.infrastructure.persistence.UtilizationThresholdPolicyJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class UtilizationThresholdPolicyPersistenceMapper {
    public UtilizationThresholdPolicyJpaEntity toJpaEntity(UtilizationThresholdPolicy d) {
        UtilizationThresholdPolicyJpaEntity e = new UtilizationThresholdPolicyJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setUnderAllocatedPercent(d.underAllocatedPercent()); e.setHealthyMinPercent(d.healthyMinPercent());
        e.setHealthyMaxPercent(d.healthyMaxPercent()); e.setWatchMaxPercent(d.watchMaxPercent());
        e.setOverloadedPercent(d.overloadedPercent()); e.setCriticalOverloadPercent(d.criticalOverloadPercent());
        e.setEnabled(d.enabled()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public UtilizationThresholdPolicy toDomain(UtilizationThresholdPolicyJpaEntity e) {
        return new UtilizationThresholdPolicy(e.getId(), e.getWorkspaceId(), e.getProjectId(),
                e.getUnderAllocatedPercent(), e.getHealthyMinPercent(), e.getHealthyMaxPercent(),
                e.getWatchMaxPercent(), e.getOverloadedPercent(), e.getCriticalOverloadPercent(),
                e.isEnabled(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
