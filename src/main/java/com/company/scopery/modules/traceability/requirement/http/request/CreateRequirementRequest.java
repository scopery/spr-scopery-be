package com.company.scopery.modules.traceability.requirement.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateRequirementRequest(
        @NotBlank String title,
        String code,
        String description,
        @NotBlank @Schema(allowableValues = {"FUNCTIONAL", "NON_FUNCTIONAL", "BUSINESS", "TECHNICAL", "SECURITY", "COMPLIANCE", "OTHER"}, example = "FUNCTIONAL") String requirementType,
        @NotBlank @Schema(allowableValues = {"CRITICAL", "HIGH", "MEDIUM", "LOW"}, example = "MEDIUM") String priority,
        UUID applicationId,
        UUID functionalItemId,
        UUID nonFunctionalItemId,
        UUID scopeItemId,
        UUID scopePackageId
) {}
