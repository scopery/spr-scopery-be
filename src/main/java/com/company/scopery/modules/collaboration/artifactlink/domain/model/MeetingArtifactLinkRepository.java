package com.company.scopery.modules.collaboration.artifactlink.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingArtifactLinkRepository {
    MeetingArtifactLink save(MeetingArtifactLink link);
    Optional<MeetingArtifactLink> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingArtifactLink> findByMeetingId(UUID meetingId);
    boolean existsActive(UUID meetingId, String targetType, UUID targetId);
}
