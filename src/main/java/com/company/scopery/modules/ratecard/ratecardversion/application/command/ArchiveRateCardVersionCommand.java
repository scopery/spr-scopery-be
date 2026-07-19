package com.company.scopery.modules.ratecard.ratecardversion.application.command;
import java.util.UUID;
public record ArchiveRateCardVersionCommand(UUID rateCardId, UUID versionId) {}
