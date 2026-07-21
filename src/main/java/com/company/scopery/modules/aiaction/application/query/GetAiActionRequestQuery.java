package com.company.scopery.modules.aiaction.application.query;

import java.util.UUID;

public record GetAiActionRequestQuery(
        UUID requestId,
        UUID actorId
) {}
