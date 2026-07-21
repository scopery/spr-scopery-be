package com.company.scopery.modules.aiaction.application.query;

import java.util.UUID;

public record GetAiActionEventListQuery(
        UUID executionId,
        long afterSequence,
        int limit,
        UUID actorId
) {}
