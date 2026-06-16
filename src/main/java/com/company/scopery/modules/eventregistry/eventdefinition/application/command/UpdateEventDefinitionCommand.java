package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

import java.util.UUID;

public record UpdateEventDefinitionCommand(
        UUID id,
        String name,
        String description,
        String inputSchema,
        String outputSchema
) {}