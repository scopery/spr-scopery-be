package com.company.scopery.modules.aiagent.eventconfig.application.command;

import java.util.UUID;

public record CreateEventConfigCommand(
        String code,
        String name,
        UUID eventDefinitionId,
        String environment,
        String triggerType,
        UUID agentId,
        UUID promptVersionId,
        UUID modelDeploymentId,
        String conditionExpression,
        String description
) {}