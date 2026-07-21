package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record BuildAiActionPlanCommand(
        UUID requestId,
        String policyCode,
        String idempotencyKey,
        UUID actorId
) {}
