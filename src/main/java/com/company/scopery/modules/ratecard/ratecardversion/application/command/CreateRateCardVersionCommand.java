package com.company.scopery.modules.ratecard.ratecardversion.application.command;
import java.time.LocalDate; import java.util.UUID;
public record CreateRateCardVersionCommand(UUID rateCardId, String name, String description,
        LocalDate effectiveFrom, LocalDate effectiveTo) {}
