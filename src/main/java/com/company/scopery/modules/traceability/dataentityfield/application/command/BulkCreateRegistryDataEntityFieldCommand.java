package com.company.scopery.modules.traceability.dataentityfield.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryDataEntityFieldCommand(
        UUID entityId,
        UUID workspaceId,
        List<CreateRegistryDataEntityFieldCommand> items
) {}
