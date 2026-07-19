package com.company.scopery.modules.quote.quoteversion.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateQuoteVersionCommand(
        UUID projectId,
        UUID quoteId,
        UUID financeScenarioId,
        String pricingMethod,
        String costBaseMethod,
        BigDecimal targetMarginPercent,
        String generateLinesFrom,
        LocalDate validUntil,
        String proposalTitle,
        String proposalNotes,
        String discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String discountReason
) {}
