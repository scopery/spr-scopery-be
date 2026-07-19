package com.company.scopery.modules.raid.decisionlink.infrastructure.mapper;

import com.company.scopery.modules.raid.decisionlink.domain.model.DecisionLink;
import com.company.scopery.modules.raid.decisionlink.infrastructure.persistence.DecisionLinkJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class DecisionLinkPersistenceMapper {
    public DecisionLink toDomain(DecisionLinkJpaEntity e) {
        return new DecisionLink(e.getId(), e.getDecisionId(), e.getProjectId(), e.getLinkType(), e.getTargetType(),
                e.getTargetId(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }

    public DecisionLinkJpaEntity toJpaEntity(DecisionLink d) {
        DecisionLinkJpaEntity e = new DecisionLinkJpaEntity();
        e.setId(d.id());
        e.setDecisionId(d.decisionId());
        e.setProjectId(d.projectId());
        e.setLinkType(d.linkType());
        e.setTargetType(d.targetType());
        e.setTargetId(d.targetId());
        e.setVersion(d.version());
        if (d.createdAt() != null) {
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
