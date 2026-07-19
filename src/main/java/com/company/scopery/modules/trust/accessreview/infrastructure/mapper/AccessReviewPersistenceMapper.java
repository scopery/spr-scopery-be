package com.company.scopery.modules.trust.accessreview.infrastructure.mapper;
import com.company.scopery.modules.trust.accessreview.domain.model.*;
import com.company.scopery.modules.trust.accessreview.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class AccessReviewPersistenceMapper {
    public AccessReviewCampaignJpaEntity toJpa(AccessReviewCampaign d) {
        AccessReviewCampaignJpaEntity e = new AccessReviewCampaignJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setName(d.name()); e.setScopeJson("{}");
        e.setStatus(d.status()); e.setStartedAt(d.startedAt()); e.setCompletedAt(d.completedAt());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public AccessReviewCampaign toDomain(AccessReviewCampaignJpaEntity e) {
        return new AccessReviewCampaign(e.getId(), e.getWorkspaceId(), e.getName(), e.getStatus(), e.getStartedAt(), e.getCompletedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public PermissionReviewFindingJpaEntity toJpa(PermissionReviewFinding d) {
        PermissionReviewFindingJpaEntity e = new PermissionReviewFindingJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCampaignId(d.campaignId());
        e.setFindingType(d.findingType()); e.setSeverity(d.severity()); e.setRecommendation(d.recommendation());
        e.setStatus(d.status()); e.setResolvedAt(d.resolvedAt()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public PermissionReviewFinding toDomain(PermissionReviewFindingJpaEntity e) {
        return new PermissionReviewFinding(e.getId(), e.getWorkspaceId(), e.getCampaignId(), e.getFindingType(), e.getSeverity(),
                e.getRecommendation(), e.getStatus(), e.getResolvedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
}
