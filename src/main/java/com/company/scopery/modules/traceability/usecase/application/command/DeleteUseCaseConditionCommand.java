package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record DeleteUseCaseConditionCommand(UUID projectId, UUID useCaseId, UUID conditionId) {}
