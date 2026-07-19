package com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.mapper;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.*;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import com.company.scopery.modules.projectbaseline.changerequestitem.infrastructure.persistence.ChangeRequestItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ChangeRequestItemPersistenceMapper {
    public ChangeRequestItem toDomain(ChangeRequestItemJpaEntity e) {
        return new ChangeRequestItem(e.getId(), e.getChangeRequestId(), e.getProjectId(),
                ChangeItemTargetType.valueOf(e.getTargetType()), e.getTargetId(),
                ChangeItemOperation.valueOf(e.getOperation()), e.getSummary(),
                e.getBeforeSnapshotJson(), e.getAfterSnapshotJson(), e.getApplyPayloadJson(),
                ChangeItemStatus.valueOf(e.getStatus()), e.getVersion()==null?0:e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public ChangeRequestItemJpaEntity toJpaEntity(ChangeRequestItem d) {
        ChangeRequestItemJpaEntity e = new ChangeRequestItemJpaEntity();
        e.setId(d.id()); e.setChangeRequestId(d.changeRequestId()); e.setProjectId(d.projectId());
        e.setTargetType(d.targetType().name()); e.setTargetId(d.targetId());
        e.setOperation(d.operation().name()); e.setSummary(d.summary());
        e.setBeforeSnapshotJson(d.beforeSnapshotJson()); e.setAfterSnapshotJson(d.afterSnapshotJson());
        e.setApplyPayloadJson(d.applyPayloadJson()); e.setStatus(d.status().name()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
