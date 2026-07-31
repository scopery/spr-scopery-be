package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.UUID;

public record UnlinkRequirementFromFunctionCommand(UUID projectId, UUID functionId, UUID requirementId) {}
