package com.company.scopery.modules.aiaction.application.query;

import java.util.UUID;

public record GetAiActionExecutionQuery(
        UUID executionId,
        UUID actorId
) {}
