package com.company.scopery.modules.aiagent.agent.application.command;

import java.util.UUID;

public record CreateAgentCommand(
        String name,
        String code,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat,
        String autonomyLevel,
        String scope,
        UUID organizationId,
        UUID workspaceId
) {
    public CreateAgentCommand(String name, String code, String type, String description,
                              UUID defaultModelDeploymentId, String outputFormat) {
        this(name, code, type, description, defaultModelDeploymentId, outputFormat,
                null, null, null, null);
    }
}
