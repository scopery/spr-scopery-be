package com.company.scopery.modules.quote.quoteversion.application.response;

import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record QuoteVersionResponse(
        UUID id,
        UUID quoteId,
        UUID projectId,
        UUID financeScenarioId,
        int versionNumber,
        String status,
        String titleSnapshot,
        String currencyCode,
        BigDecimal targetMarginPercent,
        String pricingMethod,
        String costBaseMethod,
        boolean currentFlag,
        String discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        String discountReason,
        LocalDate validUntil,
        String proposalTitle,
        String proposalNotes,
        Instant submittedAt,
        Instant approvedAt,
        Instant rejectedAt,
        String rejectionReason,
        Instant sentAt,
        Instant acceptedAt,
        Instant archivedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteVersionResponse from(QuoteVersion v) {
        return new QuoteVersionResponse(
                v.id(), v.quoteId(), v.projectId(), v.financeScenarioId(), v.versionNumber(),
                v.status().name(), v.titleSnapshot(), v.currencyCode(), v.targetMarginPercent(),
                v.pricingMethod().name(), v.costBaseMethod().name(), v.currentFlag(),
                v.discountMethod().name(), v.discountPercent(), v.discountAmount(), v.discountReason(),
                v.validUntil(), v.proposalTitle(), v.proposalNotes(),
                v.submittedAt(), v.approvedAt(), v.rejectedAt(), v.rejectionReason(),
                v.sentAt(), v.acceptedAt(), v.archivedAt(), v.createdAt(), v.updatedAt());
    }
}
