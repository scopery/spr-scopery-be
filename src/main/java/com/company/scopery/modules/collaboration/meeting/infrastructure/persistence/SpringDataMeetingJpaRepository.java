package com.company.scopery.modules.collaboration.meeting.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingJpaRepository extends JpaRepository<MeetingJpaEntity, UUID> {
    Optional<MeetingJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingJpaEntity> findByProjectIdOrderByStartAtDesc(UUID projectId);
}
