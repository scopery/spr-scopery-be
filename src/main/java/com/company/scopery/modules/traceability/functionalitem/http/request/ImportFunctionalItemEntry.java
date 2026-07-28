package com.company.scopery.modules.traceability.functionalitem.http.request;

import java.util.List;
import java.util.UUID;

public record ImportFunctionalItemEntry(
        String code,
        String title,
        String description,
        String priority,
        String type,
        List<String> acceptanceCriteria,
        UUID workspaceId,
        UUID moduleId
) {}
