package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record DeleteUseCaseBusinessRuleCommand(UUID projectId, UUID useCaseId, UUID ruleId) {}
