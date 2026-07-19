package com.company.scopery.modules.ratecard.inflationpolicy.application.command;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record CreateInflationPolicyCommand(String code, String name, String description, String scope,
        UUID organizationId, UUID workspaceId, BigDecimal inflationPercent, String compoundFrequency,
        LocalDate effectiveFrom, LocalDate effectiveTo) {}
