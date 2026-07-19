package com.company.scopery.modules.scope.deliverable.application.command;

import java.util.UUID;

public record ReopenDeliverableCommand(
        UUID projectId,
        UUID deliverableId,
        String reason
) {}
