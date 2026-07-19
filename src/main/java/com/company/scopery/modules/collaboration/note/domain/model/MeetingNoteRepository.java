package com.company.scopery.modules.collaboration.note.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingNoteRepository {
    MeetingNote save(MeetingNote note);
    Optional<MeetingNote> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingNote> findByMeetingId(UUID meetingId);
}
