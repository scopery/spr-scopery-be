package com.company.scopery.modules.quote.quoteversion.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateQuoteVersionCommand(
        UUID projectId,
        UUID quoteId,
        UUID versionId,
        String pricingMethod,
        String costBaseMethod,
        BigDecimal targetMarginPercent,
        LocalDate validUntil,
        String proposalTitle,
        String proposalNotes,
        String discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String discountReason
) {}
