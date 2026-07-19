package com.company.scopery.modules.quote.quoteterm.http.request;

public record UpdateQuoteTermRequest(
        String termType,
        String title,
        String content,
        Integer displayOrder,
        Boolean clientVisible
) {}
