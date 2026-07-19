package com.company.scopery.modules.productivity.pin.infrastructure.mapper;
import com.company.scopery.modules.productivity.pin.domain.model.PinnedItem;
import com.company.scopery.modules.productivity.pin.infrastructure.persistence.PinnedItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class PinnedItemPersistenceMapper {
    public PinnedItem toDomain(PinnedItemJpaEntity e) {
        return new PinnedItem(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getScope(), e.getOwnerUserId(), e.getTargetType(), e.getTargetId(),
                e.getSortOrder(), e.getArchivedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public PinnedItemJpaEntity toJpaEntity(PinnedItem d) {
        PinnedItemJpaEntity e = new PinnedItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setScope(d.scope());
        e.setOwnerUserId(d.ownerUserId()); e.setTargetType(d.targetType()); e.setTargetId(d.targetId());
        e.setSortOrder(d.sortOrder()); e.setArchivedAt(d.archivedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
