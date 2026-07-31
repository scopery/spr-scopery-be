package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record AddUseCaseConditionCommand(
        UUID projectId, UUID useCaseId, String conditionType, String content, int displayOrder
) {}
