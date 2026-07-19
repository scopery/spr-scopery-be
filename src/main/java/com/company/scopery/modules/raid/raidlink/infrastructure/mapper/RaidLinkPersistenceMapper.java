package com.company.scopery.modules.raid.raidlink.infrastructure.mapper;

import com.company.scopery.modules.raid.raidlink.domain.model.RaidLink;
import com.company.scopery.modules.raid.raidlink.infrastructure.persistence.RaidLinkJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RaidLinkPersistenceMapper {
    public RaidLink toDomain(RaidLinkJpaEntity e) {
        return new RaidLink(e.getId(), e.getRaidItemId(), e.getProjectId(), e.getLinkType(), e.getTargetType(),
                e.getTargetId(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }

    public RaidLinkJpaEntity toJpaEntity(RaidLink d) {
        RaidLinkJpaEntity e = new RaidLinkJpaEntity();
        e.setId(d.id());
        e.setRaidItemId(d.raidItemId());
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
