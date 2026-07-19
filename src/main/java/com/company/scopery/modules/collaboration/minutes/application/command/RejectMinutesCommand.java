package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record RejectMinutesCommand(UUID projectId, UUID minutesId, String reason) {}
