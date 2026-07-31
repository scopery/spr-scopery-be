package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record CreateUseCaseFlowCommand(
        UUID projectId,
        UUID useCaseId,
        String flowType,
        String name,
        UUID sourceStepId,
        String conditionText
) {}
