package com.company.scopery.modules.aiaction.application.command;

import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionCompensationMode;

import java.util.List;
import java.util.UUID;

public record CompensateAiActionExecutionCommand(
        UUID executionId,
        int expectedVersion,
        AiActionCompensationMode mode,
        List<UUID> targetStepIds,
        String idempotencyKey,
        UUID actorId,
        String comment
) {}
