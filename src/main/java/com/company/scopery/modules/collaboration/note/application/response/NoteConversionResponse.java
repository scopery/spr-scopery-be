package com.company.scopery.modules.collaboration.note.application.response;
import java.util.UUID;
public record NoteConversionResponse(UUID noteId, UUID meetingId, String targetType, UUID targetId, UUID artifactLinkId) {}
