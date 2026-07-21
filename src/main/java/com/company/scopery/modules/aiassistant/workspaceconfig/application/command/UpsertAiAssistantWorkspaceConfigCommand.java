package com.company.scopery.modules.aiassistant.workspaceconfig.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertAiAssistantWorkspaceConfigCommand(
        UUID workspaceId,
        UUID modelDeploymentId,
        String modelProvider,
        String modelName,
        String systemPromptOverride,
        BigDecimal temperatureOverride,
        Integer maxOutputTokensOverride
) {}
