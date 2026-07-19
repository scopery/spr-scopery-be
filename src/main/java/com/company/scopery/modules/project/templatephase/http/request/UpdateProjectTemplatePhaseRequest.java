package com.company.scopery.modules.project.templatephase.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProjectTemplatePhaseRequest(
        UUID phaseDefinitionId,
        @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull Integer displayOrder,
        Integer defaultDurationDays,
        Integer startOffsetDays,
        UUID deliverableDocumentTypeId
) {}
