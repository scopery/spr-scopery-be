package com.company.scopery.modules.aiagent.tool.application.command;

import java.util.UUID;

public record UpdateAiToolCommand(
        UUID id,
        String name,
        String description,
        String category,
        String mutationType,
        boolean requiresHumanApproval
) {}
