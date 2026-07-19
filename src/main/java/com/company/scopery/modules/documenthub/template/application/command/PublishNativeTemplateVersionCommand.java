package com.company.scopery.modules.documenthub.template.application.command;

import java.util.List;
import java.util.UUID;

public record PublishNativeTemplateVersionCommand(
        UUID workspaceId,
        UUID templateId,
        String ast,
        List<VariableDefinition> variables
) {
    public record VariableDefinition(
            String variableKey,
            String label,
            String variableType,
            boolean required,
            String defaultValue,
            boolean sensitive,
            int ordinal
    ) {}
}
