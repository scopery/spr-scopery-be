package com.company.scopery.modules.aiagent.agent.application.command;

import java.util.UUID;

public record CreateAgentCommand(
        String name,
        String code,
        String type,
        String description,
        UUID defaultModelDeploymentId,
        String outputFormat
) {}
