package com.company.scopery.modules.traceability.functionalitem.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record UpdateFunctionalItemRequest(
        @NotBlank String title,
        String description,
        String priority,
        String status,
        String type,
        List<String> acceptanceCriteria,
        UUID moduleId
) {}
