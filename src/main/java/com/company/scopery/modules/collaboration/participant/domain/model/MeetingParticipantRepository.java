package com.company.scopery.modules.collaboration.participant.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingParticipantRepository {
    MeetingParticipant save(MeetingParticipant p);
    Optional<MeetingParticipant> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingParticipant> findByMeetingId(UUID meetingId);
    void delete(MeetingParticipant p);
}
