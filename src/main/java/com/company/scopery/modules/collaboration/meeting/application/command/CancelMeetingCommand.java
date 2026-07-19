package com.company.scopery.modules.collaboration.meeting.application.command;
import java.util.UUID;
public record CancelMeetingCommand(UUID projectId, UUID meetingId, String reason) {}
