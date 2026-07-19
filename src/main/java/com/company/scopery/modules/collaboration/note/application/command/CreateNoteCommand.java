package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record CreateNoteCommand(UUID projectId, UUID meetingId, UUID agendaItemId, String noteType, String body, Boolean clientVisible) {}
