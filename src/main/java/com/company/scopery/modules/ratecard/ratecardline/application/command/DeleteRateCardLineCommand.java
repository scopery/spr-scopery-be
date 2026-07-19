package com.company.scopery.modules.ratecard.ratecardline.application.command;
import java.util.UUID;
public record DeleteRateCardLineCommand(UUID rateCardId, UUID versionId, UUID lineId) {}
