package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record PauseAiActionExecutionCommand(
        UUID executionId,
        int expectedVersion,
        String idempotencyKey,
        UUID actorId,
        String reasonCode
) {}
