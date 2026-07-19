package com.company.scopery.modules.collaboration.agendaitem.infrastructure.mapper;
import com.company.scopery.modules.collaboration.agendaitem.domain.enums.AgendaItemStatus;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItem;
import com.company.scopery.modules.collaboration.agendaitem.infrastructure.persistence.MeetingAgendaItemJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingAgendaItemPersistenceMapper {
    public MeetingAgendaItem toDomain(MeetingAgendaItemJpaEntity e) {
        return new MeetingAgendaItem(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(), e.getTitle(), e.getDescription(),
                e.getOwnerUserId(), AgendaItemStatus.valueOf(e.getStatus()), e.getSortOrder(), e.getTimeboxMinutes(), e.isClientVisible(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingAgendaItemJpaEntity toJpaEntity(MeetingAgendaItem d) {
        MeetingAgendaItemJpaEntity e = new MeetingAgendaItemJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setOwnerUserId(d.ownerUserId()); e.setStatus(d.status().name());
        e.setSortOrder(d.sortOrder()); e.setTimeboxMinutes(d.timeboxMinutes()); e.setClientVisible(d.clientVisible());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
