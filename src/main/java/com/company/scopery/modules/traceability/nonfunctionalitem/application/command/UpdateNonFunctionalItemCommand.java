package com.company.scopery.modules.traceability.nonfunctionalitem.application.command;

import java.util.UUID;

public record UpdateNonFunctionalItemCommand(
        UUID id,
        UUID projectId,
        String title,
        String description,
        String category,
        String priority,
        String status,
        String targetMetric,
        String scopeType,
        UUID scopeRefId
) {}
