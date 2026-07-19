package com.company.scopery.modules.scope.deliverable.application.command;

import java.util.UUID;

public record CreateDeliverableCommand(
        UUID projectId,
        String type,
        String code,
        String title,
        String description,
        Boolean acceptanceRequired
) {}
