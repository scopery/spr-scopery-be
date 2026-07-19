package com.company.scopery.modules.collaboration.meetingseries.application.command;
import java.util.UUID;
public record PauseMeetingSeriesCommand(UUID projectId, UUID seriesId) {}
