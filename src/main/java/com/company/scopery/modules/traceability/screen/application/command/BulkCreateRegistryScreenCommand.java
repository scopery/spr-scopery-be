package com.company.scopery.modules.traceability.screen.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryScreenCommand(
        UUID workspaceId,
        UUID applicationId,
        List<CreateRegistryScreenCommand> items
) {}
