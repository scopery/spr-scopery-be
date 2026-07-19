package com.company.scopery.modules.collaboration.actionitem.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingActionItemRepository {
    MeetingActionItem save(MeetingActionItem item);
    Optional<MeetingActionItem> findByIdAndProjectId(UUID id, UUID projectId);
    List<MeetingActionItem> findByMeetingId(UUID meetingId);
    List<MeetingActionItem> findByProjectId(UUID projectId);
}
