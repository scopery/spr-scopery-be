package com.company.scopery.modules.aiagent.eventconfig.application.command;

import java.util.UUID;

public record UpdateEventConfigCommand(
        UUID id,
        String name,
        String triggerType,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String conditionExpression,
        String description
) {}