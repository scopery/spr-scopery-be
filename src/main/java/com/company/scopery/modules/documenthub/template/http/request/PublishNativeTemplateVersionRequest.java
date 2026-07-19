package com.company.scopery.modules.documenthub.template.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PublishNativeTemplateVersionRequest(
        @NotBlank String ast,
        @Valid List<VariableDefinition> variables
) {
    public record VariableDefinition(
            @NotBlank String variableKey,
            String label,
            String variableType,
            boolean required,
            String defaultValue,
            boolean sensitive,
            int ordinal
    ) {}
}
