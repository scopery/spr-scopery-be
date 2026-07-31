package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateUseCaseConditionCommand(
        UUID projectId, UUID useCaseId, UUID conditionId, String content, int displayOrder
) {}
