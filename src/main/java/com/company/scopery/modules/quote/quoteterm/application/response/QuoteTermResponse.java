package com.company.scopery.modules.quote.quoteterm.application.response;

import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;

import java.util.UUID;

public record QuoteTermResponse(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        String termType,
        String title,
        String content,
        int displayOrder,
        boolean clientVisible
) {
    public static QuoteTermResponse from(QuoteTerm term) {
        return new QuoteTermResponse(
                term.id(), term.quoteVersionId(), term.projectId(), term.termType().name(),
                term.title(), term.content(), term.displayOrder(), term.clientVisible());
    }
}
