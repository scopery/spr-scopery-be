package com.company.scopery.modules.aiagent.prompt.application.command;

import java.util.UUID;

public record UpdatePromptVersionCommand(
        UUID id,
        String title,
        String content,
        String contentFormat,
        String variableSchema,
        String changeNote
) {}