package com.company.scopery.modules.productivity.workinbox.infrastructure.mapper;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import com.company.scopery.modules.productivity.workinbox.infrastructure.persistence.WorkInboxItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class WorkInboxItemPersistenceMapper {
    public WorkInboxItem toDomain(WorkInboxItemJpaEntity e) {
        return new WorkInboxItem(e.getId(), e.getWorkspaceId(), e.getUserId(), e.getSourceType(), e.getSourceId(), e.getActionType(), e.getTitle(),
                e.getPriority(), e.getDueAt(), e.getStatus(), e.getReadAt(), e.getDismissedAt(), e.getSnoozedUntil(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public WorkInboxItemJpaEntity toJpaEntity(WorkInboxItem d) {
        WorkInboxItemJpaEntity e = new WorkInboxItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setUserId(d.userId()); e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId());
        e.setActionType(d.actionType()); e.setTitle(d.title()); e.setPriority(d.priority()); e.setDueAt(d.dueAt()); e.setStatus(d.status());
        e.setReadAt(d.readAt()); e.setDismissedAt(d.dismissedAt()); e.setSnoozedUntil(d.snoozedUntil()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
