package com.company.scopery.modules.quote.quoteline.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateQuoteLineRequest(
        @NotBlank String lineType,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal quantity,
        @NotNull BigDecimal unitPrice,
        Integer displayOrder,
        Boolean clientVisible,
        String internalNote,
        UUID sourceProjectPhaseId
) {}
