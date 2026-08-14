package com.company.scopery.modules.traceability.screeneventitem.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateRegistryScreenEventItemRequest(
        @NotBlank String content,
        UUID modeId,
        UUID triggerFieldId,
        String triggerActionCode,
        String title,
        String conditionNote,
        UUID targetScreenId,
        String targetModeCode,
        int displayOrder) {}
