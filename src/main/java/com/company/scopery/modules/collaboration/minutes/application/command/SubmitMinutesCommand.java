package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record SubmitMinutesCommand(UUID projectId, UUID minutesId) {}
