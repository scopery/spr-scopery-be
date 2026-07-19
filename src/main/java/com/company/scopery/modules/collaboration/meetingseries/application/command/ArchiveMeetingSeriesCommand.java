package com.company.scopery.modules.collaboration.meetingseries.application.command;
import java.util.UUID;
public record ArchiveMeetingSeriesCommand(UUID projectId, UUID seriesId) {}
