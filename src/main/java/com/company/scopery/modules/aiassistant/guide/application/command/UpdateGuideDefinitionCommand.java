package com.company.scopery.modules.aiassistant.guide.application.command;

import java.util.UUID;

public record UpdateGuideDefinitionCommand(
        UUID id,
        String title,
        String bodyMarkdown,
        String status
) {}
