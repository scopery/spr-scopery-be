package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateUseCaseFlowCommand(
        UUID projectId,
        UUID useCaseId,
        UUID flowId,
        String name,
        UUID sourceStepId,
        String conditionText
) {}
