package com.company.scopery.modules.aiassistant.workspaceconfig.application.response;

import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfig;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AiAssistantWorkspaceConfigResponse(
        UUID id,
        UUID workspaceId,
        UUID modelDeploymentId,
        String modelProvider,
        String modelName,
        String systemPromptOverride,
        BigDecimal temperatureOverride,
        Integer maxOutputTokensOverride,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiAssistantWorkspaceConfigResponse from(AiAssistantWorkspaceConfig config) {
        return new AiAssistantWorkspaceConfigResponse(
                config.id(),
                config.workspaceId(),
                config.modelDeploymentId(),
                config.modelProvider(),
                config.modelName(),
                config.systemPromptOverride(),
                config.temperatureOverride(),
                config.maxOutputTokensOverride(),
                config.createdAt(),
                config.updatedAt()
        );
    }
}
