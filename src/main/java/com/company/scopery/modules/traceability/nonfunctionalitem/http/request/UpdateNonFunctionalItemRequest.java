package com.company.scopery.modules.traceability.nonfunctionalitem.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateNonFunctionalItemRequest(
        @NotBlank String title,
        String description,
        String category,
        String priority,
        String status,
        String targetMetric,
        String scopeType,
        UUID scopeRefId
) {}
