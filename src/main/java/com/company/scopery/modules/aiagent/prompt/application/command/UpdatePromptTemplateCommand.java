package com.company.scopery.modules.aiagent.prompt.application.command;

import java.util.UUID;

public record UpdatePromptTemplateCommand(
        UUID id,
        String name,
        String description
) {}