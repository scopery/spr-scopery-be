package com.company.scopery.modules.collaboration.meetingseries.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingSeriesJpaRepository extends JpaRepository<MeetingSeriesJpaEntity, UUID> {
    Optional<MeetingSeriesJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingSeriesJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
