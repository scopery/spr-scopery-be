package com.company.scopery.modules.traceability.screenfield.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryScreenFieldCommand(
        UUID screenId,
        UUID workspaceId,
        List<CreateRegistryScreenFieldCommand> items
) {}
