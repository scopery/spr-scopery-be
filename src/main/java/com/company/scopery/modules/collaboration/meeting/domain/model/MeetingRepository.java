package com.company.scopery.modules.collaboration.meeting.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingRepository {
    Meeting save(Meeting meeting);
    Optional<Meeting> findByIdAndProjectId(UUID id, UUID projectId);
    List<Meeting> findByProjectId(UUID projectId);
}
