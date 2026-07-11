package com.company.scopery.modules.project.wbs.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateWbsNodeRequest(
        @NotBlank String code,
        @NotBlank String title,
        String description,
        UUID phaseId,
        UUID parentId,
        @NotBlank String nodeType,
        int sortOrder
) {}
