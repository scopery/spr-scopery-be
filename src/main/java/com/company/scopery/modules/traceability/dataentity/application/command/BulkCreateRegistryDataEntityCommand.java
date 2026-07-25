package com.company.scopery.modules.traceability.dataentity.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryDataEntityCommand(
        UUID applicationId,
        UUID workspaceId,
        List<CreateRegistryDataEntityCommand> items
) {}
