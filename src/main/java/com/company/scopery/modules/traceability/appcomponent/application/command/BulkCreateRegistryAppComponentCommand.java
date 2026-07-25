package com.company.scopery.modules.traceability.appcomponent.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryAppComponentCommand(
        UUID applicationId,
        UUID workspaceId,
        List<CreateRegistryAppComponentCommand> items
) {}
