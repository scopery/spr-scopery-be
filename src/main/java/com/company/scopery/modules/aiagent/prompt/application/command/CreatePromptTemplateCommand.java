package com.company.scopery.modules.aiagent.prompt.application.command;

import java.util.UUID;

public record CreatePromptTemplateCommand(
        UUID agentId,
        String name,
        String code,
        String description
) {}
