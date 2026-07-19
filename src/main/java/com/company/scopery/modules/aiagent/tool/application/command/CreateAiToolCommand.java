package com.company.scopery.modules.aiagent.tool.application.command;

public record CreateAiToolCommand(
        String code,
        String name,
        String description,
        String category,
        String mutationType,
        boolean requiresHumanApproval
) {}
