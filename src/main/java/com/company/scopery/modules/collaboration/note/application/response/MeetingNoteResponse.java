package com.company.scopery.modules.collaboration.note.application.response;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNote;
import java.util.UUID;
public record MeetingNoteResponse(UUID id, UUID meetingId, UUID agendaItemId, String noteType, String body, boolean internalOnly, boolean clientVisible) {
    public static MeetingNoteResponse from(MeetingNote n) { return new MeetingNoteResponse(n.id(), n.meetingId(), n.agendaItemId(), n.noteType().name(), n.body(), n.internalOnly(), n.clientVisible()); }
}
