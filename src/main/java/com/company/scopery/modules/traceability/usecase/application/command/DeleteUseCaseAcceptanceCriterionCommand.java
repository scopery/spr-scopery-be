package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record DeleteUseCaseAcceptanceCriterionCommand(UUID projectId, UUID useCaseId, UUID criterionId) {}
