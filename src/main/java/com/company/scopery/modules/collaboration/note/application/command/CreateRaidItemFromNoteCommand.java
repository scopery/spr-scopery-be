package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record CreateRaidItemFromNoteCommand(UUID projectId, UUID meetingId, UUID noteId, String type, String title, String code, String description, UUID ownerUserId) {}
