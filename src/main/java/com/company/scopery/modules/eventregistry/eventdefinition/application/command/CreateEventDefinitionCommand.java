package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

public record CreateEventDefinitionCommand(
        String code,
        String name,
        String sourceSystem,
        String eventKey,
        String description,
        String inputSchema,
        String outputSchema
) {}