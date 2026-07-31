package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record DeleteFlowStepCommand(UUID projectId, UUID useCaseId, UUID flowId, UUID stepId) {}
