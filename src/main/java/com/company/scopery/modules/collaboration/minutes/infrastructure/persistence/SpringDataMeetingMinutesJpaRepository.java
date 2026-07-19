package com.company.scopery.modules.collaboration.minutes.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingMinutesJpaRepository extends JpaRepository<MeetingMinutesJpaEntity, UUID> {
    Optional<MeetingMinutesJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingMinutesJpaEntity> findByMeetingIdOrderByCreatedAtDesc(UUID meetingId);
}
