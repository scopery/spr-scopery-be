package com.company.scopery.modules.quote.quoteline.application.response;

import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteLineResponse(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        UUID sourcePhaseFinanceId,
        UUID sourceProjectPhaseId,
        String lineType,
        String name,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount,
        String currencyCode,
        int displayOrder,
        boolean clientVisible,
        String internalNote
) {
    public static QuoteLineResponse from(QuoteLine line) {
        return new QuoteLineResponse(
                line.id(), line.quoteVersionId(), line.projectId(), line.sourcePhaseFinanceId(),
                line.sourceProjectPhaseId(), line.lineType().name(), line.name(), line.description(),
                line.quantity(), line.unitPrice(), line.amount(), line.currencyCode(),
                line.displayOrder(), line.clientVisible(), line.internalNote());
    }
}
