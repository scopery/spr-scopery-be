package com.company.scopery.modules.traceability.requirement.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRequirementCommand(UUID projectId, List<CreateRequirementCommand> items) {}
