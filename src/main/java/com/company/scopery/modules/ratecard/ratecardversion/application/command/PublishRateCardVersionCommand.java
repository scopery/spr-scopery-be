package com.company.scopery.modules.ratecard.ratecardversion.application.command;
import java.util.UUID;
public record PublishRateCardVersionCommand(UUID rateCardId, UUID versionId) {}
