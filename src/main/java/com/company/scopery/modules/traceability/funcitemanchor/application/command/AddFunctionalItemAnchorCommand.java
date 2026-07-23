package com.company.scopery.modules.traceability.funcitemanchor.application.command;

import java.util.UUID;

public record AddFunctionalItemAnchorCommand(
        UUID functionalItemId,
        UUID projectId,
        String nodeType,
        UUID nodeId,
        String note
) {}
