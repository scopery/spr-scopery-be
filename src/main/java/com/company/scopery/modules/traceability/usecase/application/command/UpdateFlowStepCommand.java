package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateFlowStepCommand(
        UUID projectId,
        UUID useCaseId,
        UUID flowId,
        UUID stepId,
        String stepType,
        UUID screenContextId,
        String contentJson,
        UUID nextScreenId
) {}
