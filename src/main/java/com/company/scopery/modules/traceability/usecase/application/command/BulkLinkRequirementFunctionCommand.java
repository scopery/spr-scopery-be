package com.company.scopery.modules.traceability.usecase.application.command;

import java.util.List;
import java.util.UUID;

public record BulkLinkRequirementFunctionCommand(UUID projectId, UUID functionId, List<UUID> requirementIds) {}
