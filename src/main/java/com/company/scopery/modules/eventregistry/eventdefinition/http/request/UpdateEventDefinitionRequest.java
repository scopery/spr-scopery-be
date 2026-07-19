package com.company.scopery.modules.eventregistry.eventdefinition.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEventDefinitionRequest(
        @NotBlank(message = "name is required")
        @Size(max = 255)
        String name,

        String description,
        String inputSchema,
        String outputSchema,
        @Size(max = 50)
        String dataClassification,
        @Size(max = 100)
        String ownerModule
) {}
