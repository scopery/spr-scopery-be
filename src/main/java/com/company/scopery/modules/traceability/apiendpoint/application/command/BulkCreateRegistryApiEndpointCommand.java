package com.company.scopery.modules.traceability.apiendpoint.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateRegistryApiEndpointCommand(
        UUID workspaceId,
        UUID applicationId,
        List<CreateRegistryApiEndpointCommand> items
) {}
