package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record GenerateMinutesDocumentCommand(UUID projectId, UUID meetingId, UUID minutesId, UUID folderId, String code, String title) {}
