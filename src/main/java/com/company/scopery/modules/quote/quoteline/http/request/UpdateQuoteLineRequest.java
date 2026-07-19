package com.company.scopery.modules.quote.quoteline.http.request;

import java.math.BigDecimal;

public record UpdateQuoteLineRequest(
        String lineType,
        String name,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        Integer displayOrder,
        Boolean clientVisible,
        String internalNote
) {}
