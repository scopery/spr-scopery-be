package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record LinkRequirementToUseCaseCommand(UUID projectId, UUID useCaseId, UUID requirementId) {}
