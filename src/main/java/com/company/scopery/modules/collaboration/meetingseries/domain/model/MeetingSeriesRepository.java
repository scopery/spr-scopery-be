package com.company.scopery.modules.collaboration.meetingseries.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingSeriesRepository {
    MeetingSeries save(MeetingSeries series);
    Optional<MeetingSeries> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingSeries> findByProjectId(UUID projectId);
}
