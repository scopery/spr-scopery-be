package com.company.scopery.modules.collaboration.participant.domain.model;
import com.company.scopery.modules.collaboration.participant.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record MeetingParticipant(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, ParticipantTargetType targetType, UUID targetId,
        String displayNameSnapshot, String emailSnapshot, ParticipantRole participantRole, AttendanceStatus attendanceStatus,
        boolean clientVisible, int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingParticipant create(UUID workspaceId, UUID projectId, UUID meetingId, ParticipantTargetType targetType,
                                            UUID targetId, String displayName, String email, ParticipantRole role, boolean clientVisible) {
        Instant now = Instant.now();
        return new MeetingParticipant(UUID.randomUUID(), workspaceId, projectId, meetingId, targetType, targetId,
                displayName, email, role, AttendanceStatus.INVITED, clientVisible, 0, now, now);
    }
    public MeetingParticipant update(ParticipantRole role, AttendanceStatus attendance, boolean clientVisible) {
        return new MeetingParticipant(id, workspaceId, projectId, meetingId, targetType, targetId, displayNameSnapshot,
                emailSnapshot, role, attendance, clientVisible, version, createdAt, Instant.now());
    }
    public MeetingParticipant markAttended() {
        return update(participantRole, AttendanceStatus.ATTENDED, clientVisible);
    }
}
