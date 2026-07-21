package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record ExecuteAiActionPlanCommand(
        UUID planId,
        String planHash,
        int expectedPlanVersion,
        UUID confirmationId,
        String idempotencyKey,
        UUID actorId
) {}
