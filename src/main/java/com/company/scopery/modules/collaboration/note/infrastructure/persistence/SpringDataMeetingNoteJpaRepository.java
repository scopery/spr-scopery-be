package com.company.scopery.modules.collaboration.note.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingNoteJpaRepository extends JpaRepository<MeetingNoteJpaEntity, UUID> {
    Optional<MeetingNoteJpaEntity> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingNoteJpaEntity> findByMeetingIdOrderByCreatedAtAsc(UUID meetingId);
}
