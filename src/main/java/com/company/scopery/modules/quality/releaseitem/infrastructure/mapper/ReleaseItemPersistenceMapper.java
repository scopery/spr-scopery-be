package com.company.scopery.modules.quality.releaseitem.infrastructure.mapper;
import com.company.scopery.modules.quality.releaseitem.domain.enums.ReleaseItemStatus;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItem;
import com.company.scopery.modules.quality.releaseitem.infrastructure.persistence.ReleaseItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReleaseItemPersistenceMapper {
    public ReleaseItem toDomain(ReleaseItemJpaEntity e) {
        return new ReleaseItem(e.getId(), e.getProjectId(), e.getReleasePackageId(), e.getTargetType(), e.getTargetId(),
                Boolean.TRUE.equals(e.getRequired()), ReleaseItemStatus.valueOf(e.getStatus()), e.getNotes(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public ReleaseItemJpaEntity toJpaEntity(ReleaseItem d) {
        ReleaseItemJpaEntity e = new ReleaseItemJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setReleasePackageId(d.releasePackageId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setRequired(d.required());
        e.setStatus(d.status().name()); e.setNotes(d.notes());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
