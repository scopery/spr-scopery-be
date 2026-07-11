package com.company.scopery.modules.eventregistry.eventdefinition.application.response;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;

import java.time.Instant;
import java.util.UUID;

public record EventDefinitionResponse(
        UUID id,
        String code,
        String name,
        String sourceSystem,
        String eventKey,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static EventDefinitionResponse from(EventDefinition e) {
        return new EventDefinitionResponse(
                e.id(),
                e.code().value(),
                e.name(),
                e.sourceSystem().value(),
                e.eventKey().value(),
                e.description(),
                e.status().name(),
                e.createdAt(),
                e.updatedAt()
        );
    }
}