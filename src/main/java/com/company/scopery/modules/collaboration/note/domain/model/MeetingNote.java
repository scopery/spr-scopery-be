package com.company.scopery.modules.collaboration.note.domain.model;
import com.company.scopery.modules.collaboration.note.domain.enums.NoteType;
import java.time.Instant; import java.util.UUID;
public record MeetingNote(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId, NoteType noteType, String body,
        boolean internalOnly, boolean clientVisible, UUID sourceAiSuggestionId,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingNote create(UUID workspaceId, UUID projectId, UUID meetingId, UUID agendaItemId,
                                     NoteType type, String body, boolean clientVisible) {
        Instant now = Instant.now();
        return new MeetingNote(UUID.randomUUID(), workspaceId, projectId, meetingId, agendaItemId, type, body,
                !clientVisible, clientVisible, null, null, null, 0, now, now);
    }
    public MeetingNote update(NoteType type, String body, boolean clientVisible) {
        return new MeetingNote(id, workspaceId, projectId, meetingId, agendaItemId, type, body, !clientVisible,
                clientVisible, sourceAiSuggestionId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public MeetingNote archive(UUID actorId) {
        Instant now = Instant.now();
        return new MeetingNote(id, workspaceId, projectId, meetingId, agendaItemId, noteType, body, internalOnly,
                clientVisible, sourceAiSuggestionId, now, actorId, version, createdAt, now);
    }
}
