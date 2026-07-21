package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record CancelAiActionPlanCommand(
        UUID planId,
        int expectedPlanVersion,
        String reasonCode,
        String idempotencyKey,
        UUID actorId
) {}
