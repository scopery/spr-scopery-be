package com.company.scopery.modules.eventregistry.eventdefinition.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpsertEventVariablesRequest(
        @NotNull
        @Valid
        List<VariableEntryRequest> variables
) {
    public record VariableEntryRequest(
            @NotBlank(message = "variablePath is required")
            @Size(max = 255)
            String variablePath,

            @NotBlank(message = "variableLabel is required")
            @Size(max = 255)
            String variableLabel,

            @NotBlank(message = "variableType is required")
            String variableType,

            boolean required,
            boolean sensitive,
            String description,
            String exampleValue
    ) {}
}
