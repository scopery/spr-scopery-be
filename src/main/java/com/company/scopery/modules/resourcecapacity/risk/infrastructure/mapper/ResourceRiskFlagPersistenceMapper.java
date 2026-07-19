package com.company.scopery.modules.resourcecapacity.risk.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.risk.domain.model.ResourceRiskFlag;
import com.company.scopery.modules.resourcecapacity.risk.infrastructure.persistence.ResourceRiskFlagJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ResourceRiskFlagPersistenceMapper {
    public ResourceRiskFlagJpaEntity toJpaEntity(ResourceRiskFlag d) {
        ResourceRiskFlagJpaEntity e = new ResourceRiskFlagJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setResourceProfileId(d.resourceProfileId()); e.setRiskReason(d.riskReason());
        e.setImpactType(d.impactType()); e.setDescription(d.description()); e.setStatus(d.status());
        e.setMitigatedAt(d.mitigatedAt()); e.setClosedAt(d.closedAt()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public ResourceRiskFlag toDomain(ResourceRiskFlagJpaEntity e) {
        return new ResourceRiskFlag(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getResourceProfileId(),
                e.getRiskReason(), e.getImpactType(), e.getDescription(), e.getStatus(),
                e.getMitigatedAt(), e.getClosedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
