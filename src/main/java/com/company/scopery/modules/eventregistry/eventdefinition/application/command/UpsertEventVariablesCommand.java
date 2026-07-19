package com.company.scopery.modules.eventregistry.eventdefinition.application.command;

import java.util.List;
import java.util.UUID;

public record UpsertEventVariablesCommand(
        UUID eventDefinitionId,
        List<VariableEntry> variables
) {
    public record VariableEntry(
            String variablePath,
            String variableLabel,
            String variableType,
            boolean required,
            boolean sensitive,
            String description,
            String exampleValue
    ) {
        public VariableEntry(String variablePath, String variableLabel, String variableType,
                             boolean required, String description, String exampleValue) {
            this(variablePath, variableLabel, variableType, required, false, description, exampleValue);
        }
    }
}
