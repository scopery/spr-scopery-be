package com.company.scopery.modules.traceability.funcitemprop.application.response;

import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomProperty;

import java.time.Instant;
import java.util.UUID;

public record FunctionalItemCustomPropertyResponse(
        UUID id,
        UUID functionalItemId,
        String propKey,
        String propValue,
        String fieldType,
        Instant createdAt,
        Instant updatedAt
) {
    public static FunctionalItemCustomPropertyResponse from(FunctionalItemCustomProperty d) {
        return new FunctionalItemCustomPropertyResponse(
                d.id(),
                d.functionalItemId(),
                d.propKey(),
                d.propValue(),
                d.fieldType() != null ? d.fieldType().name() : null,
                d.createdAt(),
                d.updatedAt()
        );
    }
}
