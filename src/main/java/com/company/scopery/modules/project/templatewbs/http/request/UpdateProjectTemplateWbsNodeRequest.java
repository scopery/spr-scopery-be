package com.company.scopery.modules.project.templatewbs.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProjectTemplateWbsNodeRequest(
        UUID templatePhaseId,
        @Size(max = 100) String code,
        @NotBlank @Size(max = 255) String title,
        String description,
        String nodeType,
        UUID deliverableDocumentTypeId
) {}
