package com.company.scopery.modules.collaboration.meetingseries.application.command;
import java.util.UUID;
public record CreateMeetingSeriesCommand(UUID projectId, String code, String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {}
