package com.company.scopery.modules.traceability.nonfunctionalitem.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateNonFunctionalItemRequest(
        UUID workspaceId,
        @NotBlank String code,
        @NotBlank String title,
        String description,
        @NotBlank String category,
        @NotBlank String priority,
        String targetMetric,
        @NotBlank String scopeType,
        UUID scopeRefId
) {}
