package com.company.scopery.modules.traceability.functionalitem.application.command;

import java.util.List;
import java.util.UUID;

public record UpdateFunctionalItemCommand(
        UUID id,
        UUID projectId,
        UUID moduleId,
        String title,
        String description,
        String priority,
        String status,
        String type,
        List<String> acceptanceCriteria
) {}
