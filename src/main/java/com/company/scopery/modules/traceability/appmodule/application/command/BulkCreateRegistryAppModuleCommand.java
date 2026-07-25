package com.company.scopery.modules.traceability.appmodule.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryAppModuleCommand(
        UUID applicationId,
        UUID workspaceId,
        List<CreateRegistryAppModuleCommand> items
) {}
