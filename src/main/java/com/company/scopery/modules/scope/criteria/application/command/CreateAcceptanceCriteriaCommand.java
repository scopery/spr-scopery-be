package com.company.scopery.modules.scope.criteria.application.command;

import java.util.UUID;

public record CreateAcceptanceCriteriaCommand(
        UUID projectId,
        UUID deliverableId,
        String title,
        String type,
        String description,
        Boolean mandatory
) {}
