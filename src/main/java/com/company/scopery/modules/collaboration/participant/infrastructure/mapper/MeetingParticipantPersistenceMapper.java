package com.company.scopery.modules.collaboration.participant.infrastructure.mapper;
import com.company.scopery.modules.collaboration.participant.domain.enums.*;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipant;
import com.company.scopery.modules.collaboration.participant.infrastructure.persistence.MeetingParticipantJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingParticipantPersistenceMapper {
    public MeetingParticipant toDomain(MeetingParticipantJpaEntity e) {
        return new MeetingParticipant(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(),
                ParticipantTargetType.valueOf(e.getTargetType()), e.getTargetId(), e.getDisplayNameSnapshot(), e.getEmailSnapshot(),
                ParticipantRole.valueOf(e.getParticipantRole()), AttendanceStatus.valueOf(e.getAttendanceStatus()),
                e.isClientVisible(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingParticipantJpaEntity toJpaEntity(MeetingParticipant d) {
        MeetingParticipantJpaEntity e = new MeetingParticipantJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setTargetType(d.targetType().name()); e.setTargetId(d.targetId()); e.setDisplayNameSnapshot(d.displayNameSnapshot());
        e.setEmailSnapshot(d.emailSnapshot()); e.setParticipantRole(d.participantRole().name());
        e.setAttendanceStatus(d.attendanceStatus().name()); e.setClientVisible(d.clientVisible()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
