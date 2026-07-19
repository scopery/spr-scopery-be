package com.company.scopery.modules.quote.quoteterm.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateQuoteTermRequest(
        @NotBlank String termType,
        String title,
        @NotBlank String content,
        Integer displayOrder,
        Boolean clientVisible
) {}
