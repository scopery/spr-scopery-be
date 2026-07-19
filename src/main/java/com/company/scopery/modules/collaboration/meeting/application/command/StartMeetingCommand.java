package com.company.scopery.modules.collaboration.meeting.application.command;
import java.util.UUID;
public record StartMeetingCommand(UUID projectId, UUID meetingId) {}
