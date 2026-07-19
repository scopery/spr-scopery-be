package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record CreateDecisionFromNoteCommand(UUID projectId, UUID meetingId, UUID noteId, String title, String rationale, String category, String code) {}
