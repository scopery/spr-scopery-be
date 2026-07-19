package com.company.scopery.modules.collaboration.minutes.application.command;
import java.util.UUID;
public record ApproveMinutesCommand(UUID projectId, UUID minutesId) {}
