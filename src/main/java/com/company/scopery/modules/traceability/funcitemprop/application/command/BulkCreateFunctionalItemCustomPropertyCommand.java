package com.company.scopery.modules.traceability.funcitemprop.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateFunctionalItemCustomPropertyCommand(
        UUID functionalItemId,
        UUID projectId,
        List<CreateFunctionalItemCustomPropertyCommand> items
) {}
