package com.company.scopery.modules.productivity.favorite.infrastructure.mapper;
import com.company.scopery.modules.productivity.favorite.domain.model.FavoriteItem;
import com.company.scopery.modules.productivity.favorite.infrastructure.persistence.FavoriteItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class FavoriteItemPersistenceMapper {
    public FavoriteItem toDomain(FavoriteItemJpaEntity e) {
        return new FavoriteItem(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getTargetType(), e.getTargetId(), e.getLabelOverride(),
                e.getArchivedAt(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public FavoriteItemJpaEntity toJpaEntity(FavoriteItem d) {
        FavoriteItemJpaEntity e = new FavoriteItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId()); e.setTargetType(d.targetType());
        e.setTargetId(d.targetId()); e.setLabelOverride(d.labelOverride()); e.setArchivedAt(d.archivedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
