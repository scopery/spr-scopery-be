package com.company.scopery.modules.traceability.functionalitem.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ImportFunctionalItemUpdateEntry(
        @NotNull UUID existingItemId,
        String code,
        String title,
        String description,
        String priority,
        String status,
        String type,
        List<String> acceptanceCriteria,
        UUID workspaceId,
        UUID moduleId
) {}
