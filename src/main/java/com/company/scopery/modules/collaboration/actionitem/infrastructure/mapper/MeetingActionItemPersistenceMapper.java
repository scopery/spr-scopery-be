package com.company.scopery.modules.collaboration.actionitem.infrastructure.mapper;
import com.company.scopery.modules.collaboration.actionitem.domain.enums.ActionItemStatus;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItem;
import com.company.scopery.modules.collaboration.actionitem.infrastructure.persistence.MeetingActionItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingActionItemPersistenceMapper {
    public MeetingActionItem toDomain(MeetingActionItemJpaEntity e) {
        return new MeetingActionItem(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(), e.getAgendaItemId(), e.getTitle(), e.getDescription(),
                e.getOwnerTargetType(), e.getOwnerTargetId(), e.getDueDate(), ActionItemStatus.valueOf(e.getStatus()), e.getLinkedTaskId(), e.getLinkedRaidActionId(),
                e.getCompletedAt(), e.getCompletedBy(), e.getCompletionNote(), e.isClientVisible(), e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingActionItemJpaEntity toJpaEntity(MeetingActionItem d) {
        MeetingActionItemJpaEntity e = new MeetingActionItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setAgendaItemId(d.agendaItemId()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setOwnerTargetType(d.ownerTargetType()); e.setOwnerTargetId(d.ownerTargetId()); e.setDueDate(d.dueDate());
        e.setStatus(d.status().name()); e.setLinkedTaskId(d.linkedTaskId()); e.setLinkedRaidActionId(d.linkedRaidActionId());
        e.setCompletedAt(d.completedAt()); e.setCompletedBy(d.completedBy()); e.setCompletionNote(d.completionNote());
        e.setClientVisible(d.clientVisible()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
