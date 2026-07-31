package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record AddUseCaseBusinessRuleCommand(
        UUID projectId, UUID useCaseId, String ruleCode, String description, int displayOrder
) {}
