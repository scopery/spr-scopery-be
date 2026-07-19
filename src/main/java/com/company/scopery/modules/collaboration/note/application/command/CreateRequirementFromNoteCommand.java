package com.company.scopery.modules.collaboration.note.application.command;
import java.util.UUID;
public record CreateRequirementFromNoteCommand(UUID projectId, UUID meetingId, UUID noteId, UUID applicationId, String code, String title, String description, String requirementType, String priority) {}
