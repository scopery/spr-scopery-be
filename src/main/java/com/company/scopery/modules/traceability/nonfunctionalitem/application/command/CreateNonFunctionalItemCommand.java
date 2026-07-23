package com.company.scopery.modules.traceability.nonfunctionalitem.application.command;

import java.util.UUID;

public record CreateNonFunctionalItemCommand(
        UUID projectId,
        UUID workspaceId,
        String code,
        String title,
        String description,
        String category,
        String priority,
        String targetMetric,
        String scopeType,
        UUID scopeRefId
) {}
