package com.company.scopery.modules.quote.quotesummary.application.response;

import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteSummaryResponse(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        String currencyCode,
        BigDecimal costBase,
        BigDecimal directCost,
        BigDecimal overhead,
        BigDecimal subtotalBeforeDiscount,
        String discountMethod,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        BigDecimal subtotalAfterDiscount,
        String taxMode,
        BigDecimal taxAmount,
        BigDecimal totalQuotedAmount,
        BigDecimal targetMarginPercent,
        BigDecimal requiredContractValue,
        BigDecimal grossMargin,
        BigDecimal grossMarginPercent,
        BigDecimal profitBeforeTax,
        BigDecimal pbtPercent,
        String formulaVersion
) {
    public static QuoteSummaryResponse from(QuoteSummary s, boolean includeMargin) {
        return new QuoteSummaryResponse(
                s.id(), s.quoteVersionId(), s.projectId(), s.currencyCode(),
                s.costBase(), s.directCost(), s.overhead(),
                s.subtotalBeforeDiscount(), s.discountMethod().name(), s.discountPercent(),
                s.discountAmount(), s.subtotalAfterDiscount(), s.taxMode().name(), s.taxAmount(),
                s.totalQuotedAmount(), s.targetMarginPercent(), s.requiredContractValue(),
                includeMargin ? s.grossMargin() : null,
                includeMargin ? s.grossMarginPercent() : null,
                includeMargin ? s.profitBeforeTax() : null,
                includeMargin ? s.pbtPercent() : null,
                s.formulaVersion());
    }
}
