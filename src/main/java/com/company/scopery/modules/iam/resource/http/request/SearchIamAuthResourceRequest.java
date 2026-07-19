package com.company.scopery.modules.iam.resource.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search/filter parameters for listing IAM authorization resources")
public record SearchIamAuthResourceRequest(
        @Schema(description = "Full-text keyword to search in code or name", example = "Sales Bot", nullable = true)
        String keyword,

        @Schema(description = "Filter by resource type (e.g. AI_AGENT, WORKSPACE, PROJECT)", example = "AI_AGENT", nullable = true)
        String resourceType,

        @Schema(description = "Filter by resource status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"}, nullable = true)
        String status,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchIamAuthResourceRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
