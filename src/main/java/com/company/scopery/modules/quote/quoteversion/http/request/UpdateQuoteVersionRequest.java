package com.company.scopery.modules.quote.quoteversion.http.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateQuoteVersionRequest(
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
