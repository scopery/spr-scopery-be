package com.company.scopery.modules.project.wbs.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateWbsNodeRequest(
        @NotBlank String code,
        @NotBlank String title,
        String description,
        UUID phaseId,
        UUID parentId,
        @NotBlank @Schema(allowableValues = {"WORK_PACKAGE", "TASK_GROUP", "MILESTONE"}, example = "WORK_PACKAGE") String nodeType,
        int sortOrder
) {}
