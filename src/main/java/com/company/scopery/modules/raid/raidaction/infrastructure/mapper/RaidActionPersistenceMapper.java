package com.company.scopery.modules.raid.raidaction.infrastructure.mapper;
import com.company.scopery.modules.raid.raidaction.domain.enums.RaidActionStatus;
import com.company.scopery.modules.raid.raidaction.domain.model.RaidAction;
import com.company.scopery.modules.raid.raidaction.infrastructure.persistence.RaidActionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RaidActionPersistenceMapper {
    public RaidAction toDomain(RaidActionJpaEntity e) {
        return new RaidAction(e.getId(), e.getRaidItemId(), e.getProjectId(), e.getTitle(), e.getDescription(), e.getOwnerUserId(),
                e.getDueDate(), RaidActionStatus.valueOf(e.getStatus()), e.getLinkedTaskId(), e.getCompletionNote(), e.getCompletedAt(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public RaidActionJpaEntity toJpaEntity(RaidAction d) {
        RaidActionJpaEntity e = new RaidActionJpaEntity();
        e.setId(d.id()); e.setRaidItemId(d.raidItemId()); e.setProjectId(d.projectId()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setOwnerUserId(d.ownerUserId()); e.setDueDate(d.dueDate());
        e.setStatus(d.status().name()); e.setLinkedTaskId(d.linkedTaskId()); e.setCompletionNote(d.completionNote());
        e.setCompletedAt(d.completedAt()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
