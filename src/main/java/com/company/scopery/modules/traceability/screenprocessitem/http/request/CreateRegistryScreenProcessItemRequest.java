package com.company.scopery.modules.traceability.screenprocessitem.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateRegistryScreenProcessItemRequest(
        @NotBlank String content,
        UUID modeId,
        UUID targetFieldId,
        String title,
        String sourceTable,
        String conditionNote,
        int displayOrder) {}
