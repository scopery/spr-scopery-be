package com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.enums.*;
import com.company.scopery.modules.resourcecapacity.effortestimate.domain.model.EffortEstimate;
import com.company.scopery.modules.resourcecapacity.effortestimate.infrastructure.persistence.EffortEstimateJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class EffortEstimatePersistenceMapper {
    public EffortEstimateJpaEntity toJpaEntity(EffortEstimate d) {
        EffortEstimateJpaEntity e = new EffortEstimateJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setResourceRoleId(d.resourceRoleId());
        e.setResourceProfileId(d.resourceProfileId()); e.setEstimateType(d.estimateType().name());
        e.setEffortHours(d.effortHours()); e.setConfidencePercent(d.confidencePercent()); e.setReason(d.reason());
        e.setStatus(d.status().name()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public EffortEstimate toDomain(EffortEstimateJpaEntity e) {
        return new EffortEstimate(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getTargetType(), e.getTargetId(),
                e.getResourceRoleId(), e.getResourceProfileId(), EffortEstimateType.valueOf(e.getEstimateType()),
                e.getEffortHours(), e.getConfidencePercent(), e.getReason(), EffortEstimateStatus.valueOf(e.getStatus()),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
