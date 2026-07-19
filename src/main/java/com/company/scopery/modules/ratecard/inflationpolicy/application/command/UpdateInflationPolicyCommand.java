package com.company.scopery.modules.ratecard.inflationpolicy.application.command;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record UpdateInflationPolicyCommand(UUID id, String name, String description, BigDecimal inflationPercent,
        String compoundFrequency, LocalDate effectiveFrom, LocalDate effectiveTo) {}
