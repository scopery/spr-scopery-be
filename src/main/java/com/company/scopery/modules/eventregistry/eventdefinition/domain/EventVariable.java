package com.company.scopery.modules.eventregistry.eventdefinition.domain;

import java.time.Instant;
import java.util.UUID;

public class EventVariable {

    private final UUID id;
    private final UUID eventDefinitionId;
    private final String variablePath;
    private final String variableLabel;
    private final VariableType variableType;
    private final boolean required;
    private final String description;
    private final String exampleValue;
    private final Instant createdAt;
    private final Instant updatedAt;

    private EventVariable(UUID id, UUID eventDefinitionId, String variablePath, String variableLabel,
                          VariableType variableType, boolean required, String description,
                          String exampleValue, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.eventDefinitionId = eventDefinitionId;
        this.variablePath = variablePath;
        this.variableLabel = variableLabel;
        this.variableType = variableType;
        this.required = required;
        this.description = description;
        this.exampleValue = exampleValue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventVariable create(UUID eventDefinitionId, String variablePath, String variableLabel,
                                       VariableType variableType, boolean required,
                                       String description, String exampleValue) {
        Instant now = Instant.now();
        return new EventVariable(UUID.randomUUID(), eventDefinitionId, variablePath, variableLabel,
                variableType, required, description, exampleValue, now, now);
    }

    public static EventVariable reconstitute(UUID id, UUID eventDefinitionId, String variablePath,
                                             String variableLabel, VariableType variableType,
                                             boolean required, String description, String exampleValue,
                                             Instant createdAt, Instant updatedAt) {
        return new EventVariable(id, eventDefinitionId, variablePath, variableLabel,
                variableType, required, description, exampleValue, createdAt, updatedAt);
    }

    public UUID id() { return id; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public String variablePath() { return variablePath; }
    public String variableLabel() { return variableLabel; }
    public VariableType variableType() { return variableType; }
    public boolean required() { return required; }
    public String description() { return description; }
    public String exampleValue() { return exampleValue; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
