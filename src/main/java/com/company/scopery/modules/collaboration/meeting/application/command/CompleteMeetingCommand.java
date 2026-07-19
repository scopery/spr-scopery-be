package com.company.scopery.modules.collaboration.meeting.application.command;
import java.util.UUID;
public record CompleteMeetingCommand(UUID projectId, UUID meetingId) {}
