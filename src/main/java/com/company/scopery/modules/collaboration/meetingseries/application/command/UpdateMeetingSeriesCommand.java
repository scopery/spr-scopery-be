package com.company.scopery.modules.collaboration.meetingseries.application.command;
import java.util.UUID;
public record UpdateMeetingSeriesCommand(UUID projectId, UUID seriesId, String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {}
