package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

public record CreateEventDefinitionCommand(
        String code,
        String name,
        String sourceSystem,
        String eventKey,
        String description,
        String inputSchema,
        String outputSchema,
        String dataClassification,
        String ownerModule
) {
    public CreateEventDefinitionCommand(String code, String name, String sourceSystem, String eventKey,
                                        String description, String inputSchema, String outputSchema) {
        this(code, name, sourceSystem, eventKey, description, inputSchema, outputSchema, null, null);
    }
}
