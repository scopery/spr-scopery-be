package com.company.scopery.modules.collaboration.agendaitem.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MeetingAgendaItemRepository {
    MeetingAgendaItem save(MeetingAgendaItem item);
    Optional<MeetingAgendaItem> findByIdAndMeetingId(UUID id, UUID meetingId);
    List<MeetingAgendaItem> findByMeetingId(UUID meetingId);
}
