package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UpdateUseCaseBusinessRuleCommand(
        UUID projectId, UUID useCaseId, UUID ruleId, String ruleCode, String description, int displayOrder
) {}
