package com.company.scopery.modules.eventregistry.eventdefinition.application.response;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinition;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EventDefinitionDetailResponse(
        UUID id,
        String code,
        String name,
        String sourceSystem,
        String eventKey,
        String description,
        String inputSchema,
        String outputSchema,
        String status,
        int eventVersion,
        String samplePayloadJson,
        List<EventVariableResponse> variables,
        Instant createdAt,
        Instant updatedAt
) {

    public static EventDefinitionDetailResponse from(EventDefinition e) {
        return from(e, List.of());
    }

    public static EventDefinitionDetailResponse from(EventDefinition e, List<EventVariableResponse> variables) {
        return new EventDefinitionDetailResponse(
                e.id(),
                e.code().value(),
                e.name(),
                e.sourceSystem().value(),
                e.eventKey().value(),
                e.description(),
                e.inputSchema(),
                e.outputSchema(),
                e.status().name(),
                e.eventVersion(),
                e.samplePayloadJson(),
                variables,
                e.createdAt(),
                e.updatedAt()
        );
    }
}
