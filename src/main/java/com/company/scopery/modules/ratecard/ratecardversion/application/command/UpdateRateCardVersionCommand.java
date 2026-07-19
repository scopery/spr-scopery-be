package com.company.scopery.modules.ratecard.ratecardversion.application.command;
import java.time.LocalDate; import java.util.UUID;
public record UpdateRateCardVersionCommand(UUID rateCardId, UUID versionId, String name, String description,
        LocalDate effectiveFrom, LocalDate effectiveTo) {}
