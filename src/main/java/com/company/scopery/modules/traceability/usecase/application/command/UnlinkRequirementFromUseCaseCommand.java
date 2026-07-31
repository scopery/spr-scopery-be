package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UnlinkRequirementFromUseCaseCommand(UUID projectId, UUID useCaseId, UUID requirementId) {}
