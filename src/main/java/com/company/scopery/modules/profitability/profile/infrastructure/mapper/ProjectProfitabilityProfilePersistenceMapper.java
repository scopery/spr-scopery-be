package com.company.scopery.modules.profitability.profile.infrastructure.mapper;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfile;
import com.company.scopery.modules.profitability.profile.infrastructure.persistence.ProjectProfitabilityProfileJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ProjectProfitabilityProfilePersistenceMapper {
    public ProjectProfitabilityProfile toDomain(ProjectProfitabilityProfileJpaEntity e) {
        return new ProjectProfitabilityProfile(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCurrency(), e.getTrackingMode(),
                e.getRevenueMode(), e.getCostMode(), e.getOwnerUserId(), e.getPortalVisibility(), e.getStatus(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ProjectProfitabilityProfileJpaEntity toJpaEntity(ProjectProfitabilityProfile d) {
        ProjectProfitabilityProfileJpaEntity e = new ProjectProfitabilityProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setCurrency(d.currency());
        e.setTrackingMode(d.trackingMode()); e.setRevenueMode(d.revenueMode()); e.setCostMode(d.costMode());
        e.setOwnerUserId(d.ownerUserId()); e.setPortalVisibility(d.portalVisibility()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
