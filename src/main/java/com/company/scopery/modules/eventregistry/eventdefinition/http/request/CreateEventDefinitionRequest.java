package com.company.scopery.modules.eventregistry.eventdefinition.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEventDefinitionRequest(
        @NotBlank(message = "code is required")
        @Size(max = 100)
        String code,

        @NotBlank(message = "name is required")
        @Size(max = 255)
        String name,

        @NotBlank(message = "sourceSystem is required")
        @Size(max = 100)
        String sourceSystem,

        @NotBlank(message = "eventKey is required")
        @Size(max = 150)
        String eventKey,

        String description,
        String inputSchema,
        String outputSchema
) {}
