package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

public record UpdateEventDefinitionCommand(
        java.util.UUID id,
        String name,
        String description,
        String inputSchema,
        String outputSchema,
        String dataClassification,
        String ownerModule
) {
    public UpdateEventDefinitionCommand(java.util.UUID id, String name, String description,
                                        String inputSchema, String outputSchema) {
        this(id, name, description, inputSchema, outputSchema, null, null);
    }
}
