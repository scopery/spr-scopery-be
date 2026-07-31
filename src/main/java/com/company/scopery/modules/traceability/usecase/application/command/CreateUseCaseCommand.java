package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record CreateUseCaseCommand(
        UUID projectId,
        UUID primaryFunctionId,
        String key,
        String name,
        String goal,
        String primaryActorName,
        String triggerText
) {}
