package com.company.scopery.modules.aiaction.application.query;

import java.util.UUID;

public record GetAiActionPlanQuery(
        UUID planId,
        UUID actorId
) {}
