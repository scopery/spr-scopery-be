package com.company.scopery.modules.collaboration.artifactlink.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataMeetingArtifactLinkJpaRepository extends JpaRepository<MeetingArtifactLinkJpaEntity, UUID> {
    Optional<MeetingArtifactLinkJpaEntity> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingArtifactLinkJpaEntity> findByMeetingIdAndArchivedAtIsNullOrderByCreatedAtAsc(UUID meetingId);
    @Query("select count(e) > 0 from MeetingArtifactLinkJpaEntity e where e.meetingId = :meetingId and e.targetType = :targetType and e.targetId = :targetId and e.archivedAt is null")
    boolean existsActive(@Param("meetingId") UUID meetingId, @Param("targetType") String targetType, @Param("targetId") UUID targetId);
}
