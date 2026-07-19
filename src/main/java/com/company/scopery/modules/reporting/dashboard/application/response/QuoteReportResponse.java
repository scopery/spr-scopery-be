package com.company.scopery.modules.reporting.dashboard.application.response;

import java.math.BigDecimal;

public record QuoteReportResponse(
        Object currentQuoteVersionId,
        Boolean sourceAvailable,
        String quoteStatus,
        BigDecimal targetMarginPercent,
        BigDecimal discountAmount,
        String validUntil,
        String sentAt,
        String acceptedAt,
        BigDecimal totalQuotedAmount,
        BigDecimal grossMarginPercent,
        BigDecimal pbtPercent
) {}
