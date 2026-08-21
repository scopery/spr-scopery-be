package com.company.scopery.modules.traceability.requirement.application.command;

import java.util.List;
import java.util.UUID;

public record BulkDeleteRequirementCommand(UUID projectId, List<UUID> ids) {}
