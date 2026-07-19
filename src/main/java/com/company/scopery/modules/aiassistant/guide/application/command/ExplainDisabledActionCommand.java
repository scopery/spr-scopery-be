package com.company.scopery.modules.aiassistant.guide.application.command;

import java.util.UUID;

public record ExplainDisabledActionCommand(
        UUID actorId,
        UUID workspaceId,
        UUID projectId,
        String pageCode,
        String actionCode,
        String locale
) {}
