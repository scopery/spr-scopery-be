package com.company.scopery.modules.collaboration.meeting.infrastructure.mapper;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingStatus;
import com.company.scopery.modules.collaboration.meeting.domain.enums.MeetingType;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.infrastructure.persistence.MeetingJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingPersistenceMapper {
    public Meeting toDomain(MeetingJpaEntity e) {
        return new Meeting(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingSeriesId(), e.getTitle(),
                e.getDescription(), MeetingType.valueOf(e.getMeetingType()), MeetingStatus.valueOf(e.getStatus()),
                e.getStartAt(), e.getEndAt(), e.getTimezone(), e.getLocation(), e.getMeetingUrl(), e.getOrganizerUserId(),
                e.isClientVisible(), e.isInternalOnly(), e.getCancelledAt(), e.getCancelledBy(), e.getCancelReason(),
                e.getArchivedAt(), e.getArchivedBy(), e.getTraceId(), e.getVersion()==null?0:e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingJpaEntity toJpaEntity(Meeting d) {
        MeetingJpaEntity e = new MeetingJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setMeetingSeriesId(d.meetingSeriesId()); e.setTitle(d.title()); e.setDescription(d.description());
        e.setMeetingType(d.meetingType().name()); e.setStatus(d.status().name()); e.setStartAt(d.startAt());
        e.setEndAt(d.endAt()); e.setTimezone(d.timezone()); e.setLocation(d.location()); e.setMeetingUrl(d.meetingUrl());
        e.setOrganizerUserId(d.organizerUserId()); e.setClientVisible(d.clientVisible()); e.setInternalOnly(d.internalOnly());
        e.setCancelledAt(d.cancelledAt()); e.setCancelledBy(d.cancelledBy()); e.setCancelReason(d.cancelReason());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
