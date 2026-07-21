package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record ConfirmAiActionPlanCommand(
        UUID planId,
        String planHash,
        int expectedPlanVersion,
        String decision,
        String channel,
        String comment,
        String idempotencyKey,
        UUID actorId
) {}
