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
        int eventVersion,
        String dataClassification,
        String ownerModule,
        boolean systemEvent,
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
                e.eventVersion(),
                e.dataClassification() != null ? e.dataClassification().name() : null,
                e.ownerModule(),
                e.systemEvent(),
                e.createdAt(),
                e.updatedAt()
        );
    }
}
