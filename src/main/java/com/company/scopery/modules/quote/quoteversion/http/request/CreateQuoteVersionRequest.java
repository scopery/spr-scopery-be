package com.company.scopery.modules.quote.quoteversion.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateQuoteVersionRequest(
        @NotNull UUID financeScenarioId,
        @NotBlank String pricingMethod,
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
