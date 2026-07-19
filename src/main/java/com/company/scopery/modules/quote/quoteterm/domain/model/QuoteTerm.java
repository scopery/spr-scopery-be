package com.company.scopery.modules.quote.quoteterm.domain.model;

import com.company.scopery.modules.quote.quoteterm.domain.enums.QuoteTermType;

import java.time.Instant;
import java.util.UUID;

public record QuoteTerm(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        QuoteTermType termType,
        String title,
        String content,
        int displayOrder,
        boolean clientVisible,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteTerm create(
            UUID quoteVersionId,
            UUID projectId,
            QuoteTermType termType,
            String title,
            String content,
            int displayOrder,
            boolean clientVisible) {
        return new QuoteTerm(
                UUID.randomUUID(), quoteVersionId, projectId, termType, title, content,
                displayOrder, clientVisible, 0, null, null);
    }

    public QuoteTerm update(QuoteTermType termType, String title, String content,
                             Integer displayOrder, Boolean clientVisible) {
        return new QuoteTerm(
                id, quoteVersionId, projectId,
                termType == null ? this.termType : termType,
                title,
                content == null ? this.content : content,
                displayOrder == null ? this.displayOrder : displayOrder,
                clientVisible == null ? this.clientVisible : clientVisible,
                version, createdAt, updatedAt);
    }

    public QuoteTerm withDisplayOrder(int order) {
        return new QuoteTerm(id, quoteVersionId, projectId, termType, title, content,
                order, clientVisible, version, createdAt, updatedAt);
    }
}
