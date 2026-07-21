package com.company.scopery.modules.aiaction.application.command;

import java.util.UUID;

public record ProcessAiActionStepCommand(
        UUID executionId,
        int stepOrdinal
) {}
