package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record ArchiveNoteCommand(UUID projectId, UUID meetingId, UUID noteId) {}
