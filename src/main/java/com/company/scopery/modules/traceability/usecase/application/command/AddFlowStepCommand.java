package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record AddFlowStepCommand(
        UUID projectId,
        UUID useCaseId,
        UUID flowId,
        String stepType,
        UUID screenContextId,
        String contentJson,
        UUID nextScreenId,
        int displayOrder
) {}
