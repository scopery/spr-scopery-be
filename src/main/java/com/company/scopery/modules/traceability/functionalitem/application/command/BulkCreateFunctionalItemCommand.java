package com.company.scopery.modules.traceability.functionalitem.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateFunctionalItemCommand(
        UUID projectId,
        List<CreateFunctionalItemCommand> items
) {}
