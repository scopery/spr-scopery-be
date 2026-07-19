package com.company.scopery.modules.collaboration.participant.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingParticipantJpaRepository extends JpaRepository<MeetingParticipantJpaEntity, UUID> {
    Optional<MeetingParticipantJpaEntity> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingParticipantJpaEntity> findByMeetingIdOrderByCreatedAtAsc(UUID meetingId);
}
