package com.company.scopery.modules.collaboration.agendaitem.application.response;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.MeetingAgendaItem;
import java.util.UUID;
public record MeetingAgendaItemResponse(UUID id, UUID meetingId, String title, String description, UUID ownerUserId, String status, int sortOrder, Integer timeboxMinutes, boolean clientVisible) {
    public static MeetingAgendaItemResponse from(MeetingAgendaItem a) {
        return new MeetingAgendaItemResponse(a.id(), a.meetingId(), a.title(), a.description(), a.ownerUserId(), a.status().name(), a.sortOrder(), a.timeboxMinutes(), a.clientVisible());
    }
}
