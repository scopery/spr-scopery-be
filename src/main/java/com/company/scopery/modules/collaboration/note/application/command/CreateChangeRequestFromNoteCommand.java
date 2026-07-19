package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record CreateChangeRequestFromNoteCommand(UUID projectId, UUID meetingId, UUID noteId, String code, String title, String description, String changeType, String priority, UUID baselineId, String reason) {}
