package com.company.scopery.modules.traceability.functionalitem.application.command;

import java.util.List;
import java.util.UUID;

public record CreateFunctionalItemCommand(
        UUID projectId,
        UUID workspaceId,
        UUID moduleId,
        String code,
        String title,
        String description,
        String priority,
        String type,
        List<String> acceptanceCriteria
) {}
