package com.company.scopery.modules.aiassistant.guide.application.command;

import java.util.UUID;

public record ExplainFieldCommand(
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        String pageCode,
        String fieldCode,
        String locale
) {}
