package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record ReorderFlowStepsCommand(UUID projectId, UUID useCaseId, UUID flowId, List<UUID> stepIds) {}
