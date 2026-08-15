package com.company.scopery.modules.traceability.componentfield.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryComponentFieldCommand(
        UUID componentId,
        UUID workspaceId,
        List<CreateRegistryComponentFieldCommand> items
) {}
