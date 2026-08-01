package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateUseCaseCommand(
        UUID projectId,
        UUID useCaseId,
        String name,
        String goal,
        String primaryActorName,
        String triggerText,
        String status,
        /** When non-null, set as the Use Case primary Function. Null = leave unchanged. */
        UUID primaryFunctionId
) {}
