package com.company.scopery.modules.traceability.nonfunctionalitem.application.command;

import java.util.List;
import java.util.UUID;

public record BulkCreateNonFunctionalItemCommand(
        UUID projectId,
        List<CreateNonFunctionalItemCommand> items
) {}
