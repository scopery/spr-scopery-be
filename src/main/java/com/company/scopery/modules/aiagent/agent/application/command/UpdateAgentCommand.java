package com.company.scopery.modules.aiagent.agent.application.command;

import java.util.UUID;

public record UpdateAgentCommand(
        UUID id,
        String name,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat,
        String autonomyLevel,
        String scope,
        UUID organizationId,
        UUID workspaceId
) {
    public UpdateAgentCommand(UUID id, String name, String type, String description,
                              UUID defaultModelDeploymentId, String outputFormat) {
        this(id, name, type, description, defaultModelDeploymentId, outputFormat,
                null, null, null, null);
    }
}
