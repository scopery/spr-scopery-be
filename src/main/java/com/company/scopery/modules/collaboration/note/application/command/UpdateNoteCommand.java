package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record UpdateNoteCommand(UUID projectId, UUID meetingId, UUID noteId, String noteType, String body, Boolean clientVisible) {}
