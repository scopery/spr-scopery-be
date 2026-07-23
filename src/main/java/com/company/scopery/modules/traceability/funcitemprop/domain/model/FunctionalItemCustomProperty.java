package com.company.scopery.modules.traceability.funcitemprop.domain.model;

import com.company.scopery.modules.traceability.funcitemprop.domain.enums.CustomPropertyFieldType;

import java.time.Instant;
import java.util.UUID;

public record FunctionalItemCustomProperty(
        UUID id,
        UUID functionalItemId,
        String propKey,
        String propValue,
        CustomPropertyFieldType fieldType,
        Instant createdAt,
        Instant updatedAt
) {
    public static FunctionalItemCustomProperty create(
            UUID functionalItemId,
            String propKey,
            String propValue,
            CustomPropertyFieldType fieldType
    ) {
        Instant now = Instant.now();
        return new FunctionalItemCustomProperty(
                UUID.randomUUID(),
                functionalItemId,
                propKey,
                propValue,
                fieldType,
                now,
                now
        );
    }

    public FunctionalItemCustomProperty withUpdated(String propValue, CustomPropertyFieldType fieldType) {
        return new FunctionalItemCustomProperty(
                this.id,
                this.functionalItemId,
                this.propKey,
                propValue,
                fieldType,
                this.createdAt,
                Instant.now()
        );
    }
}
