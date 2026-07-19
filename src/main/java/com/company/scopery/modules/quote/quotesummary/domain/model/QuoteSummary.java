package com.company.scopery.modules.quote.quotesummary.domain.model;

import com.company.scopery.modules.quote.quotesummary.domain.enums.TaxMode;
import com.company.scopery.modules.quote.quoteversion.domain.enums.DiscountMethod;
import com.company.scopery.modules.quote.shared.constant.QuoteFormulaVersions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record QuoteSummary(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        String currencyCode,
        BigDecimal costBase,
        BigDecimal directCost,
        BigDecimal overhead,
        BigDecimal subtotalBeforeDiscount,
        DiscountMethod discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        BigDecimal subtotalAfterDiscount,
        TaxMode taxMode,
        BigDecimal taxAmount,
        BigDecimal totalQuotedAmount,
        BigDecimal targetMarginPercent,
        BigDecimal requiredContractValue,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        String formulaVersion,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteSummary create(
            UUID quoteVersionId,
            UUID projectId,
            String currencyCode,
            BigDecimal costBase,
            BigDecimal directCost,
            BigDecimal overhead,
            BigDecimal subtotalBeforeDiscount,
            DiscountMethod discountMethod,
            BigDecimal discountPercent,
            BigDecimal discountAmount,
            BigDecimal subtotalAfterDiscount,
            BigDecimal totalQuotedAmount,
            BigDecimal targetMarginPercent,
            BigDecimal requiredContractValue,
            BigDecimal grossMargin,
            BigDecimal grossMarginPercent,
            BigDecimal profitBeforeTax,
            BigDecimal pbtPercent) {
        return new QuoteSummary(
                UUID.randomUUID(), quoteVersionId, projectId, currencyCode,
                nz(costBase), nz(directCost), nz(overhead), nz(subtotalBeforeDiscount),
                discountMethod == null ? DiscountMethod.NONE : discountMethod,
                discountPercent, nz(discountAmount), nz(subtotalAfterDiscount),
                TaxMode.TAX_EXCLUDED, null, nz(totalQuotedAmount),
                targetMarginPercent, requiredContractValue,
                grossMargin, grossMarginPercent, profitBeforeTax, pbtPercent,
                QuoteFormulaVersions.QUOTE_V1, null, null);
    }

    public QuoteSummary withId(UUID existingId) {
        return new QuoteSummary(
                existingId, quoteVersionId, projectId, currencyCode, costBase, directCost, overhead,
                subtotalBeforeDiscount, discountMethod, discountPercent, discountAmount,
                subtotalAfterDiscount, taxMode, taxAmount, totalQuotedAmount,
                targetMarginPercent, requiredContractValue, grossMargin, grossMarginPercent,
                profitBeforeTax, pbtPercent, formulaVersion, createdAt, updatedAt);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
