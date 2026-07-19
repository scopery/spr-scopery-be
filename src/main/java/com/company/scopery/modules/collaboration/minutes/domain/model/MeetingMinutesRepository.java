package com.company.scopery.modules.collaboration.minutes.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingMinutesRepository {
    MeetingMinutes save(MeetingMinutes minutes);
    Optional<MeetingMinutes> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingMinutes> findByMeetingId(UUID meetingId);
}
