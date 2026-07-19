package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record UpdateMinutesCommand(UUID projectId, UUID minutesId, String summary, String decisions, String actions, String clientSummary) {}
