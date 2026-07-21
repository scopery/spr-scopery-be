package com.company.scopery.modules.aiaction.application.query;

import java.util.UUID;

public record GetAiActionStepListQuery(
        UUID planId,
        UUID actorId
) {}
