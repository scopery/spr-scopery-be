package com.company.scopery.modules.productivity.recent.infrastructure.mapper;
import com.company.scopery.modules.productivity.recent.domain.model.RecentItem;
import com.company.scopery.modules.productivity.recent.infrastructure.persistence.RecentItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RecentItemPersistenceMapper {
    public RecentItem toDomain(RecentItemJpaEntity e) {
        return new RecentItem(e.getId(), e.getWorkspaceId(), e.getPrincipalType(), e.getUserId(), e.getExternalPortalAccountId(),
                e.getTargetType(), e.getTargetId(), e.getTitleSnapshot(), e.getViewedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RecentItemJpaEntity toJpaEntity(RecentItem d) {
        RecentItemJpaEntity e = new RecentItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setPrincipalType(d.principalType()); e.setUserId(d.userId());
        e.setExternalPortalAccountId(d.externalPortalAccountId()); e.setTargetType(d.targetType()); e.setTargetId(d.targetId());
        e.setTitleSnapshot(d.titleSnapshot()); e.setViewedAt(d.viewedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
