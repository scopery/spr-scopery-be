package com.company.scopery.modules.collaboration.actionitem.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingActionItemJpaRepository extends JpaRepository<MeetingActionItemJpaEntity, UUID> {
    Optional<MeetingActionItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingActionItemJpaEntity> findByMeetingIdOrderByCreatedAtAsc(UUID meetingId);
    List<MeetingActionItemJpaEntity> findByProjectIdOrderByDueDateAsc(UUID projectId);
}
