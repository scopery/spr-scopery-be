package com.company.scopery.modules.eventregistry.eventdefinition.application.response;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;

import java.util.UUID;

public record EventVariableResponse(
        UUID id,
        UUID eventDefinitionId,
        String variablePath,
        String variableLabel,
        String variableType,
        boolean required,
        String description,
        String exampleValue
) {
    public static EventVariableResponse from(EventVariable v) {
        return new EventVariableResponse(
                v.id(),
                v.eventDefinitionId(),
                v.variablePath(),
                v.variableLabel(),
                v.variableType().name(),
                v.required(),
                v.description(),
                v.exampleValue()
        );
    }
}
