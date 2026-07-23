package com.company.scopery.modules.traceability.functionalitem.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateFunctionalItemRequest(
        @NotBlank String code,
        @NotBlank String title,
        String description,
        @NotNull String priority,
        @NotNull String type,
        List<String> acceptanceCriteria,
        UUID workspaceId,
        UUID moduleId
) {}
