package com.company.scopery.modules.aiagent.agent.application.command;

import java.util.UUID;

public record UpdateAgentCommand(
        UUID id,
        String name,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat
) {}
