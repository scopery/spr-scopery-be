package com.company.scopery.modules.traceability.nonfunctionalitem.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateNonFunctionalItemRequest(
        UUID workspaceId,
        @NotBlank String code,
        @NotBlank String title,
        String description,
        @NotBlank @Schema(allowableValues = {"PERFORMANCE", "SECURITY", "USABILITY", "RELIABILITY", "MAINTAINABILITY", "SCALABILITY", "COMPATIBILITY", "OTHER"}, example = "PERFORMANCE") String category,
        @NotBlank @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM") String priority,
        String targetMetric,
        @NotBlank @Schema(allowableValues = {"SYSTEM", "MODULE", "FEATURE"}, example = "SYSTEM") String scopeType,
        UUID scopeRefId
) {}
