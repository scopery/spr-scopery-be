package com.company.scopery.modules.collaboration.participant.application.response;
import com.company.scopery.modules.collaboration.participant.domain.model.MeetingParticipant;
import java.time.Instant; import java.util.UUID;
public record MeetingParticipantResponse(UUID id, UUID meetingId, String targetType, UUID targetId, String displayNameSnapshot,
        String emailSnapshot, String participantRole, String attendanceStatus, boolean clientVisible, Instant createdAt) {
    public static MeetingParticipantResponse from(MeetingParticipant p) {
        return new MeetingParticipantResponse(p.id(), p.meetingId(), p.targetType().name(), p.targetId(), p.displayNameSnapshot(),
                p.emailSnapshot(), p.participantRole().name(), p.attendanceStatus().name(), p.clientVisible(), p.createdAt());
    }
}
