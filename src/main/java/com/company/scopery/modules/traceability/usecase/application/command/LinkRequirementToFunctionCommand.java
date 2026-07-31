package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record LinkRequirementToFunctionCommand(UUID projectId, UUID functionId, UUID requirementId) {}
