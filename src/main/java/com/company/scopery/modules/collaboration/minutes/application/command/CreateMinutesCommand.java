package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record CreateMinutesCommand(UUID projectId, UUID meetingId, String summary, String decisions, String actions, String clientSummary) {}
